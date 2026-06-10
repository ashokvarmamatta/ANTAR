package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Apps
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppsViewModel(private val appsRepository: AppsRepository) : ViewModel() {
    private val _appsState = MutableStateFlow<Apps?>(null)
    val appsState = _appsState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadApps() {
        if (_appsState.value != null || _isLoading.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val apps = appsRepository.getApps()
                _appsState.value = apps
            } finally {
                _isLoading.value = false
            }
        }
    }
}
