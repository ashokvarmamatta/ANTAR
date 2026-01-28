package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository

class AppsViewModel(private val appsRepository: AppsRepository) : ViewModel() {
    fun getApps() = appsRepository.getApps()
}
