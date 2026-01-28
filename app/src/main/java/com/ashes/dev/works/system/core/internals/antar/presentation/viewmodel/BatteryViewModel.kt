package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository

class BatteryViewModel(private val batteryRepository: BatteryRepository) : ViewModel() {
    fun getBattery() = batteryRepository.getBattery()
}
