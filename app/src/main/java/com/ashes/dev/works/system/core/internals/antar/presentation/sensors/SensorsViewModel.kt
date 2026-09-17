package com.ashes.dev.works.system.core.internals.antar.presentation.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetSensorsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorsViewModel(
    private val getSensors: GetSensorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SensorsUiState>(SensorsUiState.Loading)
    val uiState: StateFlow<SensorsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = SensorsUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getSensors()) {
                is AppResult.Success ->
                    if (result.data.isEmpty()) SensorsUiState.Empty else SensorsUiState.Content(result.data)
                is AppResult.Failure -> SensorsUiState.Error(result.error.messageRes())
            }
        }
    }
}
