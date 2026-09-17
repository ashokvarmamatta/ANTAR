package com.ashes.dev.works.system.core.internals.antar.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveGpsEnabledUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveLocationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModel(
    private val observeLocation: ObserveLocationUseCase,
    private val observeGpsEnabled: ObserveGpsEnabledUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    /**
     * Reported by the screen; null until it has checked. Precise access decides which providers and
     * GNSS listeners the repository registers, so a change restarts the stream.
     */
    private val preciseAccess = MutableStateFlow<Boolean?>(null)

    // Created once per ViewModel so recompositions don't re-register the GPS/GNSS listeners,
    // and WhileSubscribed stops them 5s after the screen leaves the foreground.
    val uiState: StateFlow<LocationUiState> =
        combine(retryTrigger, preciseAccess.filterNotNull().distinctUntilChanged()) { attempt, _ -> attempt }
            .flatMapLatest { locationState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocationUiState.Loading)

    fun load() {
        retryTrigger.update { it + 1 }
    }

    fun onPreciseAccessChanged(granted: Boolean) {
        preciseAccess.value = granted
    }

    private fun locationState(): Flow<LocationUiState> {
        val fixes: Flow<AppResult<Location>?> = observeLocation()
            .map<AppResult<Location>, AppResult<Location>?> { it }
            .onStart { emit(null) }
        return combine(observeGpsEnabled(), fixes) { gpsEnabled, fix -> toUiState(gpsEnabled, fix) }
    }

    private fun toUiState(gpsEnabled: Boolean, fix: AppResult<Location>?): LocationUiState = when (fix) {
        null -> LocationUiState.Content(LocationDetails(location = null, isGpsEnabled = gpsEnabled))
        is AppResult.Success -> LocationUiState.Content(LocationDetails(location = fix.data, isGpsEnabled = gpsEnabled))
        is AppResult.Failure -> LocationUiState.Error(fix.error.messageRes())
    }
}
