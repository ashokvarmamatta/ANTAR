package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard

import com.ashes.dev.works.system.core.internals.antar.domain.model.DashboardSummary

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Content(val summary: DashboardSummary) : DashboardUiState
}

/** The app shell leaves its splash once the first dashboard reading is in. */
val DashboardUiState.isReady: Boolean get() = this is DashboardUiState.Content
