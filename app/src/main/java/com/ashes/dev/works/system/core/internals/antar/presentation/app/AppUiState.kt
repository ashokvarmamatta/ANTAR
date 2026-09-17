package com.ashes.dev.works.system.core.internals.antar.presentation.app

import com.ashes.dev.works.system.core.internals.antar.domain.model.AppSettings

sealed interface AppUiState {
    /** Settings not read yet: the system splash stays on screen. */
    data object Loading : AppUiState

    data class Ready(val settings: AppSettings) : AppUiState
}
