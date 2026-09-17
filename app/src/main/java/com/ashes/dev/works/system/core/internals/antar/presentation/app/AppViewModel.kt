package com.ashes.dev.works.system.core.internals.antar.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.CompleteIntroUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** App-shell state: theme, motion level and whether onboarding has been seen. */
class AppViewModel(
    observeSettings: ObserveSettingsUseCase,
    private val completeIntro: CompleteIntroUseCase
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = observeSettings()
        .map { AppUiState.Ready(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppUiState.Loading)

    fun finishIntro() {
        viewModelScope.launch { completeIntro() }
    }
}
