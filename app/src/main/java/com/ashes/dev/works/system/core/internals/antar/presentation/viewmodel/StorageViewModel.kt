package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository

class StorageViewModel(private val storageRepository: StorageRepository) : ViewModel() {
    fun getStorage() = storageRepository.getStorage()
}
