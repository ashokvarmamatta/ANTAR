package com.ashes.dev.works.system.core.internals.antar.presentation.network

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState

/** Everything the Network tab shows. [wifi] is null when the Wi-Fi stream could not be read. */
data class NetworkDetails(
    val connection: ActiveConnection,
    val wifi: WifiState?,
    val telephony: TelephonyInfo
)

sealed interface NetworkUiState {
    data object Loading : NetworkUiState
    data class Content(val details: NetworkDetails) : NetworkUiState
    data class Error(@param:StringRes val message: Int) : NetworkUiState
}
