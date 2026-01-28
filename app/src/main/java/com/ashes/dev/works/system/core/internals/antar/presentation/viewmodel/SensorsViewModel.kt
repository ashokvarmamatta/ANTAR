package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository

class SensorsViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {
    fun getSensors() = sensorsRepository.getSensors()
}
