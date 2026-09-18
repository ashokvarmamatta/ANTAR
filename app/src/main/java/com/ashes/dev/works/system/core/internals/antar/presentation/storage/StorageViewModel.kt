package com.ashes.dev.works.system.core.internals.antar.presentation.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.presentation.common.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetStorageInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StorageViewModel(
    private val getStorageInfo: GetStorageInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StorageUiState>(StorageUiState.Loading)
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = StorageUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getStorageInfo()) {
                is AppResult.Success -> StorageUiState.Content(result.data)
                is AppResult.Failure -> StorageUiState.Error(result.error.messageRes())
            }
        }
    }
}
