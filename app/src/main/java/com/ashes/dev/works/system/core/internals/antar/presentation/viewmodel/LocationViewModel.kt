package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    fun getLocation() = locationRepository.getLocation()
}
