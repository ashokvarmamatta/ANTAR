package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.presentation.common.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryRecord
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.LogBatteryReadingUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveBatteryHistoryUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveBatteryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class BatteryViewModel(
    private val observeBattery: ObserveBatteryUseCase,
    private val observeBatteryHistory: ObserveBatteryHistoryUseCase,
    private val logBatteryReading: LogBatteryReadingUseCase
) : ViewModel() {

    private data class HistoryWindow(val nowMillis: Long, val records: List<BatteryRecord>)

    private data class Selections(val metric: BatteryMetric, val isMetricGraphVisible: Boolean)

    private val retryTrigger = MutableStateFlow(0)
    private val selectedMetric = MutableStateFlow(BatteryMetric.CURRENT)
    private val metricGraphVisible = MutableStateFlow(false) // Hidden by default
    private val historyRange = MutableStateFlow(HistoryRange.HOURS_24)

    // The rolling live histories live in the ViewModel and survive a pause; they just don't grow
    // while nobody is watching. Only touched from the collecting coroutine (main thread).
    private var samples = LiveSamples()

    /** Re-anchors the 24 h / 7 day windows so a screen left open keeps sliding with the clock. */
    private val clock: Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(HISTORY_REFRESH_MS)
        }
    }

    private val weekWindow: Flow<HistoryWindow> = clock.flatMapLatest { now ->
        observeBatteryHistory(now - WEEK_MS)
            .map { records -> HistoryWindow(now, records) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(HistoryWindow(now, emptyList()))
            }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    private val history: Flow<BatteryHistory> =
        combine(weekWindow, historyRange) { window, range -> buildHistory(window, range) }
            .distinctUntilChanged()

    private val chargingSessions: Flow<List<ChargingSession>> =
        weekWindow.map { extractChargingSessions(it.records) }.distinctUntilChanged()

    private val live: Flow<Pair<AppResult<Battery>, LiveSamples>> =
        retryTrigger.flatMapLatest { observeBattery() }
            .map { result ->
                if (result is AppResult.Success) samples = samples.append(result.data)
                result to samples
            }

    private val selections: Flow<Selections> =
        combine(selectedMetric, metricGraphVisible) { metric, visible -> Selections(metric, visible) }

    // Live readings only flow while the Battery screen is collecting (WhileSubscribed), so the
    // receiver + 2s poll stop in the background.
    val uiState: StateFlow<BatteryUiState> =
        combine(live, history, chargingSessions, selections) { (result, liveSamples), historyState, sessions, selection ->
            when (result) {
                is AppResult.Success -> BatteryUiState.Content(
                    battery = result.data,
                    live = liveSamples,
                    history = historyState,
                    chargingSessions = sessions,
                    selectedMetric = selection.metric,
                    isMetricGraphVisible = selection.isMetricGraphVisible
                )
                is AppResult.Failure -> BatteryUiState.Error(result.error.messageRes())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BatteryUiState.Loading)

    init {
        // Log battery on app open so we get an immediate data point
        viewModelScope.launch {
            try {
                logBatteryReading()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Best effort: the background worker logs again on its next run.
            }
        }
    }

    fun selectMetric(metric: BatteryMetric) {
        selectedMetric.value = metric
    }

    fun toggleMetricGraph() {
        metricGraphVisible.value = !metricGraphVisible.value
    }

    fun selectHistoryRange(range: HistoryRange) {
        historyRange.value = range
    }

    fun retry() {
        retryTrigger.value += 1
    }

    private fun LiveSamples.append(battery: Battery): LiveSamples = LiveSamples(
        currentMicroAmps = currentMicroAmps.appendCapped(battery.currentMicroAmps),
        powerWatts = powerWatts.appendCapped(battery.powerWatts),
        temperatureDeciCelsius = temperatureDeciCelsius.appendCapped(battery.temperatureDeciCelsius),
        remainingCapacityMah = remainingCapacityMah.appendCapped(battery.remainingCapacityMah)
    )

    private fun <T> List<T>.appendCapped(value: T?): List<T> =
        if (value == null) this else (this + value).takeLast(LIVE_SAMPLE_LIMIT)

    private fun buildHistory(window: HistoryWindow, range: HistoryRange): BatteryHistory {
        val since = when (range) {
            HistoryRange.HOURS_24 -> window.nowMillis - DAY_MS
            HistoryRange.DAYS_7 -> window.nowMillis - WEEK_MS
        }
        val records = window.records.filter { it.timestampMillis >= since }
        if (records.isEmpty()) return BatteryHistory.empty(range)

        val discharging = records.filter { !it.isCharging }
        val drainPerHour = if (discharging.size >= 2) {
            val hours = (discharging.last().timestampMillis - discharging.first().timestampMillis) / MILLIS_PER_HOUR
            if (hours > 0) (discharging.first().levelPercent - discharging.last().levelPercent) / hours else null
        } else {
            null
        }

        return BatteryHistory(
            range = range,
            points = records.map { HistoryPoint(it.timestampMillis, it.levelPercent, it.isCharging) },
            averageLevelPercent = records.map { it.levelPercent }.average(),
            minLevelPercent = records.minOf { it.levelPercent },
            maxLevelPercent = records.maxOf { it.levelPercent },
            drainPercentPerHour = drainPerHour?.takeIf { it > 0 }
        )
    }

    private fun extractChargingSessions(records: List<BatteryRecord>): List<ChargingSession> {
        if (records.size < 2) return emptyList()

        val sessions = mutableListOf<ChargingSession>()
        var sessionStart: BatteryRecord? = null

        for (i in records.indices) {
            val record = records[i]
            val start = sessionStart
            if (record.isCharging && start == null) {
                sessionStart = record
            } else if (!record.isCharging && start != null) {
                val prev = records[i - 1]
                sessions.add(
                    ChargingSession(
                        startTimeMillis = start.timestampMillis,
                        endTimeMillis = prev.timestampMillis,
                        startLevelPercent = start.levelPercent,
                        endLevelPercent = prev.levelPercent
                    )
                )
                sessionStart = null
            }
        }

        // If still charging at the end
        val openStart = sessionStart
        if (openStart != null) {
            val last = records.last()
            sessions.add(
                ChargingSession(
                    startTimeMillis = openStart.timestampMillis,
                    endTimeMillis = last.timestampMillis,
                    startLevelPercent = openStart.levelPercent,
                    endLevelPercent = last.levelPercent
                )
            )
        }

        return sessions.takeLast(MAX_SESSIONS).asReversed().toList()
    }

    companion object {
        const val LIVE_SAMPLE_LIMIT = 100
        const val MAX_SESSIONS = 10
        private val DAY_MS = TimeUnit.HOURS.toMillis(24)
        private val WEEK_MS = TimeUnit.DAYS.toMillis(7)
        private val HISTORY_REFRESH_MS = TimeUnit.MINUTES.toMillis(15)
        private const val MILLIS_PER_HOUR = 3_600_000.0
    }
}
