package com.ashes.dev.works.system.core.internals.antar.presentation.network

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.ConnectionCard
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.MobileDataCard
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.NetworkHeader
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.SimInfoCard
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.WifiCard
import com.ashes.dev.works.system.core.internals.antar.presentation.network.components.WifiIdentityCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NetworkScreen(viewModel: NetworkViewModel = koinViewModel()) {
    var requestedOnce by rememberSaveable { mutableStateOf(false) }
    val wifiAccess = rememberMultiplePermissionsState(WIFI_IDENTITY_PERMISSIONS) { requestedOnce = true }
    val wifiAccessGranted = wifiAccess.permissions.any {
        it.permission == Manifest.permission.ACCESS_FINE_LOCATION && it.status.isGranted
    }
    LaunchedEffect(wifiAccessGranted) { viewModel.onWifiAccessChanged(wifiAccessGranted) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "networkState"
    ) { state ->
        when (state) {
            NetworkUiState.Loading -> LoadingSkeleton()
            is NetworkUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is NetworkUiState.Content -> NetworkContent(
                details = state.details,
                wifiAccessGranted = wifiAccessGranted,
                wifiAccessPermanentlyDenied = requestedOnce && !wifiAccess.shouldShowRationale,
                onRequestWifiAccess = wifiAccess::launchMultiplePermissionRequest
            )
        }
    }
}

@Composable
private fun NetworkContent(
    details: NetworkDetails,
    wifiAccessGranted: Boolean,
    wifiAccessPermanentlyDenied: Boolean,
    onRequestWifiAccess: () -> Unit
) {
    val connection = details.connection
    val wifi = details.wifi
    val wifiLink = wifi?.connection

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            NetworkHeader(connection = connection, wifiLink = wifiLink, modifier = Modifier.staggeredEntry(0))
        }

        item(key = "connection") {
            ConnectionCard(connection = connection, modifier = Modifier.staggeredEntry(1))
        }

        if (wifi != null) {
            item(key = "wifi") {
                WifiCard(wifi = wifi, modifier = Modifier.staggeredEntry(2))
            }
        }

        if (wifiLink != null) {
            item(key = "wifiIdentity") {
                WifiIdentityCard(
                    link = wifiLink,
                    accessGranted = wifiAccessGranted,
                    permanentlyDenied = wifiAccessPermanentlyDenied,
                    onRequestAccess = onRequestWifiAccess,
                    modifier = Modifier.staggeredEntry(3)
                )
            }
        }

        item(key = "mobileData") {
            MobileDataCard(telephony = details.telephony, modifier = Modifier.staggeredEntry(4))
        }

        details.telephony.sim?.let { sim ->
            item(key = "sim") {
                SimInfoCard(sim = sim, modifier = Modifier.staggeredEntry(5))
            }
        }
    }
}

/**
 * Android only reveals the connected network's SSID, BSSID and security to apps holding precise
 * location. COARSE is requested with FINE because Android 12+ ignores a FINE-only request.
 */
private val WIFI_IDENTITY_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)
