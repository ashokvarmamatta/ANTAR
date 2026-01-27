package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository

class DeviceViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    fun getDevice() = deviceRepository.getDevice()
}