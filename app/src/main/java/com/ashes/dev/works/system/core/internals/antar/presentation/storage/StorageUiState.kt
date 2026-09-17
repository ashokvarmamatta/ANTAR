package com.ashes.dev.works.system.core.internals.antar.presentation.storage

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo

sealed interface StorageUiState {
    data object Loading : StorageUiState
    data class Content(val storage: StorageInfo) : StorageUiState
    data class Error(@param:StringRes val message: Int) : StorageUiState
}
