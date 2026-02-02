package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

enum class BatteryMetric {
    CURRENT, POWER, TEMPERATURE
}

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
    }

    fun setMetric(metric: BatteryMetric) {
        _selectedMetric.value = metric
    }

    fun toggleMetricGraph() {
        _showMetricGraph.value = !_showMetricGraph.value
    }
}
