package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BatteryViewModel(private val batteryRepository: BatteryRepository) : ViewModel() {

    private val _batteryInfo = MutableStateFlow<Battery?>(null)
    val batteryInfo = _batteryInfo.asStateFlow()

    private val _currentHistory = MutableStateFlow<List<Int>>(emptyList())
    val currentHistory = _currentHistory.asStateFlow()

    init {
        batteryRepository.getBatteryInfo()
            .onEach { battery ->
                _batteryInfo.value = battery
                val newHistory = (_currentHistory.value + battery.current).takeLast(20)
                _currentHistory.value = newHistory
            }
            .launchIn(viewModelScope)
    }
}
