package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository

class NetworkViewModel(private val networkRepository: NetworkRepository) : ViewModel() {
    fun getNetwork() = networkRepository.getNetwork()
}
