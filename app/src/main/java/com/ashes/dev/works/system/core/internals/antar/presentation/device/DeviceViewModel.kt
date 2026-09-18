package com.ashes.dev.works.system.core.internals.antar.presentation.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.presentation.common.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetDeviceInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(
    private val getDeviceInfo: GetDeviceInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceUiState>(DeviceUiState.Loading)
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = DeviceUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getDeviceInfo()) {
                is AppResult.Success -> DeviceUiState.Content(result.data)
                is AppResult.Failure -> DeviceUiState.Error(result.error.messageRes())
            }
        }
    }
}
