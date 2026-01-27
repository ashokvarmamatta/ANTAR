package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository

class DisplayViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    fun getDisplay() = deviceRepository.getDisplay()
}