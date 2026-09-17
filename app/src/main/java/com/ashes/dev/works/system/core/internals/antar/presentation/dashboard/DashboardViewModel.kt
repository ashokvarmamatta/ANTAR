package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(observeDashboard: ObserveDashboardUseCase) : ViewModel() {

    // WhileSubscribed: the battery receiver + 2s poll behind this stop 5s after the UI goes to the
    // background, instead of running for as long as the ViewModel lives.
    val uiState: StateFlow<DashboardUiState> = observeDashboard()
        .map<_, DashboardUiState> { DashboardUiState.Content(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)
}
