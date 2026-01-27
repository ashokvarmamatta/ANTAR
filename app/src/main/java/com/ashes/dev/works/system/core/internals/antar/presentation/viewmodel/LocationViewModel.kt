package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository

class LocationViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    fun getLocation() = deviceRepository.getLocation()
}