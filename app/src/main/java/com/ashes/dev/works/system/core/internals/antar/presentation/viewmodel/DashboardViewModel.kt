package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DashboardViewModel(private val dashboardRepository: DashboardRepository) : ViewModel() {

    private val _dashboardInfo = MutableStateFlow<Dashboard?>(null)
    val dashboardInfo = _dashboardInfo.asStateFlow()

    init {
        dashboardRepository.getDashboardInfo()
            .onEach { dashboard ->
                _dashboardInfo.value = dashboard
            }
            .launchIn(viewModelScope)
    }
}
