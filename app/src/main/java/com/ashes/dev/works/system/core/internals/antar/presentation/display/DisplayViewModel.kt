package com.ashes.dev.works.system.core.internals.antar.presentation.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetDisplayInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DisplayViewModel(
    private val getDisplayInfo: GetDisplayInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DisplayUiState>(DisplayUiState.Loading)
    val uiState: StateFlow<DisplayUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = DisplayUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getDisplayInfo()) {
                is AppResult.Success -> DisplayUiState.Content(result.data)
                is AppResult.Failure -> DisplayUiState.Error(result.error.messageRes())
            }
        }
    }
}
