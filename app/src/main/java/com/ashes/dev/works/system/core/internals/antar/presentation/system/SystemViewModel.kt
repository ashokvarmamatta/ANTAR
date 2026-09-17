package com.ashes.dev.works.system.core.internals.antar.presentation.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetSystemInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveUptimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SystemViewModel(
    private val getSystemInfo: GetSystemInfoUseCase,
    observeUptime: ObserveUptimeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SystemUiState>(SystemUiState.Loading)
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()

    /** Live uptime; null until the first tick, when the screen falls back to the loaded value. */
    val uptimeMillis: StateFlow<Long?> = observeUptime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    init {
        load()
    }

    fun load() {
        _uiState.value = SystemUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getSystemInfo()) {
                is AppResult.Success -> SystemUiState.Content(result.data)
                is AppResult.Failure -> SystemUiState.Error(result.error.messageRes())
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
