package com.ashes.dev.works.system.core.internals.antar.presentation.display

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.DisplayInfo

sealed interface DisplayUiState {
    data object Loading : DisplayUiState
    data class Content(val display: DisplayInfo) : DisplayUiState
    data class Error(@param:StringRes val message: Int) : DisplayUiState
}
