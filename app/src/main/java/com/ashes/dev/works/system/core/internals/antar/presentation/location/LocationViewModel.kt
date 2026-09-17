package com.ashes.dev.works.system.core.internals.antar.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LocationViewModel(locationRepository: LocationRepository) : ViewModel() {

    // Created once per ViewModel so recompositions don't re-register the GPS/GNSS listeners,
    // and WhileSubscribed stops them 5s after the screen leaves the foreground.
    val location: StateFlow<Location> = locationRepository.getLocation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EMPTY_LOCATION)

    val isGpsEnabled: StateFlow<Boolean> = locationRepository.isGpsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private companion object {
        val EMPTY_LOCATION = Location(
            satellites = emptyList(), latitude = "- - -",
            longitude = "- - -", altitude = "- - -", seaLevelAltitude = "- - -",
            speed = "- - -", speedAccurate = "- - -", pdop = "- - -",
            timeToFirstFix = "", ehvDop = "- - -", hvAccurate = "- - -",
            numberOfSatellites = "- - -", bearing = "- - -", bearingAccurate = "- - -",
            address = "- - -"
        )
    }
}
