package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StorageViewModel(private val storageRepository: StorageRepository) : ViewModel() {
    private val _storageState = MutableStateFlow<Storage?>(null)
    val storageState = _storageState.asStateFlow()

    init {
        refreshStorage()
    }

    fun refreshStorage() {
        viewModelScope.launch {
            _storageState.value = storageRepository.getStorage()
        }
    }
}
