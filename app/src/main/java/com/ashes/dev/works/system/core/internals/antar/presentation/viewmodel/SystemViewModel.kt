package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository

class SystemViewModel(private val systemRepository: SystemRepository) : ViewModel() {
    fun getSystem() = systemRepository.getSystem()
}
