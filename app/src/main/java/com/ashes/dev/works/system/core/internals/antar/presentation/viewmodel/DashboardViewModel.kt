package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DashboardRepository

class DashboardViewModel(private val dashboardRepository: DashboardRepository) : ViewModel() {
    fun getDashboard() = dashboardRepository.getDashboard()
}
