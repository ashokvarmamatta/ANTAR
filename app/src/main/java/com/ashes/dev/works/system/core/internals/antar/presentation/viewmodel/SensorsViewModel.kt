package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Sensors
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorsViewModel(private val sensorsRepository: SensorsRepository) : ViewModel() {
    private val _sensorsState = MutableStateFlow<Sensors?>(null)
    val sensorsState = _sensorsState.asStateFlow()

    init {
        loadSensors()
    }

    private fun loadSensors() {
        viewModelScope.launch(Dispatchers.IO) {
            val sensors = sensorsRepository.getSensors()
            _sensorsState.value = sensors
        }
    }
}
