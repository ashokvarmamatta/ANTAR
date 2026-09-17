package com.ashes.dev.works.system.core.internals.antar.presentation.location

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location

/** [location] is null until the first fix arrives. */
data class LocationDetails(
    val location: Location?,
    val isGpsEnabled: Boolean
)

sealed interface LocationUiState {
    data object Loading : LocationUiState
    data class Content(val details: LocationDetails) : LocationUiState
    data class Error(@param:StringRes val message: Int) : LocationUiState
}
