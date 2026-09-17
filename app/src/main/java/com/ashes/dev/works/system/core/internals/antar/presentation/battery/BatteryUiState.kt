package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery

enum class BatteryMetric { CURRENT, POWER, TEMPERATURE }

enum class HistoryRange { HOURS_24, DAYS_7 }

/** A stretch of consecutive charging readings from the background log. */
@Immutable
data class ChargingSession(
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val startLevelPercent: Int,
    val endLevelPercent: Int
) {
    val durationMinutes: Long get() = (endTimeMillis - startTimeMillis) / 60_000
    val gainedPercent: Int get() = endLevelPercent - startLevelPercent
}

/** One point of the stored-history chart. */
@Immutable
data class HistoryPoint(
    val timestampMillis: Long,
    val levelPercent: Int,
    val isCharging: Boolean
)

/** Stored history for the selected range, with its stats already computed. */
@Immutable
data class BatteryHistory(
    val range: HistoryRange,
    val points: List<HistoryPoint>,
    val averageLevelPercent: Double?,
    val minLevelPercent: Int?,
    val maxLevelPercent: Int?,
    /** Percent lost per hour across the discharging readings; null when it cannot be measured. */
    val drainPercentPerHour: Double?
) {
    /** The chart needs two points to draw a line. */
    val hasChart: Boolean get() = points.size >= 2

    companion object {
        fun empty(range: HistoryRange) = BatteryHistory(range, emptyList(), null, null, null, null)
    }
}

/** Rolling live samples (last [BatteryViewModel.LIVE_SAMPLE_LIMIT] readings) for the live charts. */
@Immutable
data class LiveSamples(
    val currentMicroAmps: List<Int> = emptyList(),
    val powerWatts: List<Double> = emptyList(),
    val temperatureDeciCelsius: List<Int> = emptyList(),
    val remainingCapacityMah: List<Int> = emptyList()
)

sealed interface BatteryUiState {
    data object Loading : BatteryUiState

    @Immutable
    data class Content(
        val battery: Battery,
        val live: LiveSamples,
        val history: BatteryHistory,
        /** Most recent first, at most [BatteryViewModel.MAX_SESSIONS]. */
        val chargingSessions: List<ChargingSession>,
        val selectedMetric: BatteryMetric,
        val isMetricGraphVisible: Boolean
    ) : BatteryUiState

    data class Error(@param:StringRes val message: Int) : BatteryUiState
}
