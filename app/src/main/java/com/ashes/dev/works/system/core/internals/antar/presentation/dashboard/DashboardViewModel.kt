package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(dashboardRepository: DashboardRepository) : ViewModel() {

    // WhileSubscribed: the battery receiver + 2s poll behind this stop 5s after the UI goes to the
    // background, instead of running for as long as the ViewModel lives.
    val dashboardInfo: StateFlow<Dashboard?> = dashboardRepository.getDashboardInfo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
