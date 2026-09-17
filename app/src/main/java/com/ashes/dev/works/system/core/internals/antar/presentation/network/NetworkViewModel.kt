package com.ashes.dev.works.system.core.internals.antar.presentation.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetTelephonyInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveActiveConnectionUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveWifiStateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkViewModel(
    private val getTelephonyInfo: GetTelephonyInfoUseCase,
    private val observeActiveConnection: ObserveActiveConnectionUseCase,
    private val observeWifiState: ObserveWifiStateUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    /** Reported by the screen; null until it has checked. A change re-registers the Wi-Fi callback. */
    private val wifiAccessGranted = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<NetworkUiState> = retryTrigger
        .flatMapLatest { attempt -> networkState(showLoading = attempt > 0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkUiState.Loading)

    fun load() {
        retryTrigger.update { it + 1 }
    }

    /**
     * The platform only fills in SSID, BSSID and security for callbacks registered while location
     * access is held, so a grant restarts the Wi-Fi stream.
     */
    fun onWifiAccessChanged(granted: Boolean) {
        wifiAccessGranted.value = granted
    }

    private fun networkState(showLoading: Boolean): Flow<NetworkUiState> = flow {
        if (showLoading) emit(NetworkUiState.Loading)
        when (val telephony = getTelephonyInfo()) {
            is AppResult.Failure -> emit(NetworkUiState.Error(telephony.error.messageRes()))
            is AppResult.Success -> {
                val telephonyInfo = telephony.data
                emitAll(
                    combine(observeActiveConnection(), wifiStates()) { connection, wifi ->
                        toUiState(connection, wifi, telephonyInfo)
                    }
                )
            }
        }
    }

    private fun wifiStates(): Flow<AppResult<WifiState>> = wifiAccessGranted
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { observeWifiState() }

    private fun toUiState(
        connection: AppResult<ActiveConnection>,
        wifi: AppResult<WifiState>,
        telephony: TelephonyInfo
    ): NetworkUiState = when (connection) {
        is AppResult.Failure -> NetworkUiState.Error(connection.error.messageRes())
        is AppResult.Success -> NetworkUiState.Content(
            NetworkDetails(
                connection = connection.data,
                // A Wi-Fi read failure hides the Wi-Fi cards instead of failing the whole tab.
                wifi = when (wifi) {
                    is AppResult.Success -> wifi.data
                    is AppResult.Failure -> null
                },
                telephony = telephony
            )
        )
    }
}
