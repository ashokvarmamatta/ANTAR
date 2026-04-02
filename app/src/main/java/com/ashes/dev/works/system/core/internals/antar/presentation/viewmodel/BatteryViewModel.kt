package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class BatteryMetric {
    CURRENT, POWER, TEMPERATURE
}

enum class HistoryRange {
    HOURS_24, DAYS_7
}

data class ChargingSession(
    val startTime: Long,
    val endTime: Long,
    val startLevel: Int,
    val endLevel: Int
)

class BatteryViewModel(private val batteryRepository: BatteryRepository) : ViewModel() {

    private val _batteryInfo = MutableStateFlow<Battery?>(null)
    val batteryInfo = _batteryInfo.asStateFlow()

    private val _currentHistory = MutableStateFlow<List<Int>>(emptyList())
    val currentHistory = _currentHistory.asStateFlow()

    private val _powerHistory = MutableStateFlow<List<Double>>(emptyList())
    val powerHistory = _powerHistory.asStateFlow()

    private val _tempHistory = MutableStateFlow<List<Int>>(emptyList())
    val tempHistory = _tempHistory.asStateFlow()

    private val _capacityHistory = MutableStateFlow<List<Int>>(emptyList())
    val capacityHistory = _capacityHistory.asStateFlow()

    private val _selectedMetric = MutableStateFlow(BatteryMetric.CURRENT)
    val selectedMetric = _selectedMetric.asStateFlow()

    private val _showMetricGraph = MutableStateFlow(false) // Hidden by default
    val showMetricGraph = _showMetricGraph.asStateFlow()

    private val _selectedHistoryRange = MutableStateFlow(HistoryRange.HOURS_24)
    val selectedHistoryRange = _selectedHistoryRange.asStateFlow()

    // 24-hour history from DB
    val history24h = batteryRepository.getBatteryHistory(
        System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7-day history from DB
    val history7d = batteryRepository.getBatteryHistory(
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Charging sessions derived from 7-day history
    val chargingSessions = history7d.map { logs -> extractChargingSessions(logs) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        batteryRepository.getBatteryInfo()
            .onEach { battery ->
                _batteryInfo.value = battery

                _currentHistory.value = (_currentHistory.value + battery.current).takeLast(100)
                _powerHistory.value = (_powerHistory.value + battery.power).takeLast(100)
                _tempHistory.value = (_tempHistory.value + battery.temperature).takeLast(100)
                _capacityHistory.value = (_capacityHistory.value + battery.remainingCapacity).takeLast(100)
            }
            .launchIn(viewModelScope)

        // Log battery on app open so we get an immediate data point
        viewModelScope.launch {
            batteryRepository.logCurrentBattery()
        }
    }

    fun setMetric(metric: BatteryMetric) {
        _selectedMetric.value = metric
    }

    fun toggleMetricGraph() {
        _showMetricGraph.value = !_showMetricGraph.value
    }

    fun setHistoryRange(range: HistoryRange) {
        _selectedHistoryRange.value = range
    }

    private fun extractChargingSessions(logs: List<BatteryLog>): List<ChargingSession> {
        if (logs.size < 2) return emptyList()

        val sessions = mutableListOf<ChargingSession>()
        var sessionStart: BatteryLog? = null

        for (i in logs.indices) {
            val log = logs[i]
            if (log.isCharging && sessionStart == null) {
                sessionStart = log
            } else if (!log.isCharging && sessionStart != null) {
                val prev = logs[i - 1]
                sessions.add(
                    ChargingSession(
                        startTime = sessionStart.timestamp,
                        endTime = prev.timestamp,
                        startLevel = sessionStart.batteryLevel,
                        endLevel = prev.batteryLevel
                    )
                )
                sessionStart = null
            }
        }

        // If still charging at the end
        if (sessionStart != null) {
            val last = logs.last()
            sessions.add(
                ChargingSession(
                    startTime = sessionStart.timestamp,
                    endTime = last.timestamp,
                    startLevel = sessionStart.batteryLevel,
                    endLevel = last.batteryLevel
                )
            )
        }

        return sessions.takeLast(10) // Show last 10 sessions
    }
}
