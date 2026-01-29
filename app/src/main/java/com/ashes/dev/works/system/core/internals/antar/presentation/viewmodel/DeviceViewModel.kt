package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DeviceViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    val device: StateFlow<Device> = deviceRepository.getDeviceFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = deviceRepository.getDevice()
        )
}
