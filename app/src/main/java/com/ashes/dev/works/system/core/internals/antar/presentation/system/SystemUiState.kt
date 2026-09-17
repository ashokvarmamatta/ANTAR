package com.ashes.dev.works.system.core.internals.antar.presentation.system

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.SystemInfo

sealed interface SystemUiState {
    data object Loading : SystemUiState
    data class Content(val info: SystemInfo) : SystemUiState
    data class Error(@param:StringRes val message: Int) : SystemUiState
}
