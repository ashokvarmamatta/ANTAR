package com.ashes.dev.works.system.core.internals.antar.presentation.location

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.common.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.AddressCard
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.GpsDisabledBanner
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.LocationHeader
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.PositionCard
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.SatelliteRow
import com.ashes.dev.works.system.core.internals.antar.presentation.location.components.SatellitesCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(viewModel: LocationViewModel = koinViewModel()) {
    // Approximate-only access already shows coordinates and accuracy, so either grant opens the tab.
    PermissionGate(
        permissions = LOCATION_PERMISSIONS,
        icon = Icons.Outlined.LocationOn,
        title = R.string.location_permission_title,
        body = R.string.location_permission_body,
        primingTitle = R.string.location_priming_title,
        primingPoints = LOCATION_PRIMING_POINTS,
        isGranted = { st -> st.permissions.any { it.status.isGranted } }
    ) {
        LocationGranted(viewModel)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationGranted(viewModel: LocationViewModel) {
    var requestedOnce by rememberSaveable { mutableStateOf(false) }
    val access = rememberMultiplePermissionsState(LOCATION_PERMISSIONS) { requestedOnce = true }
    val precise = access.permissions.any {
        it.permission == Manifest.permission.ACCESS_FINE_LOCATION && it.status.isGranted
    }
    LaunchedEffect(precise) { viewModel.onPreciseAccessChanged(precise) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "locationState"
    ) { state ->
        when (state) {
            LocationUiState.Loading -> LoadingSkeleton()
            is LocationUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is LocationUiState.Content -> LocationContent(
                details = state.details,
                precise = precise,
                preciseAccessPermanentlyDenied = requestedOnce && !access.shouldShowRationale,
                onRequestPreciseAccess = access::launchMultiplePermissionRequest
            )
        }
    }
}

@Composable
private fun LocationContent(
    details: LocationDetails,
    precise: Boolean,
    preciseAccessPermanentlyDenied: Boolean,
    onRequestPreciseAccess: () -> Unit
) {
    val location = details.location

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = !details.isGpsEnabled) {
            GpsDisabledBanner()
        }

        AdaptiveCardGrid(modifier = Modifier.weight(1f)) {
            item(key = "header", span = StaggeredGridItemSpan.FullLine) {
                LocationHeader(location = location, modifier = Modifier.staggeredEntry(0))
            }

            if (location != null) {
                item(key = "satellites") {
                    SatellitesCard(
                        satellites = location.satellites,
                        precise = precise,
                        permanentlyDenied = preciseAccessPermanentlyDenied,
                        onRequestPreciseAccess = onRequestPreciseAccess,
                        modifier = Modifier.staggeredEntry(1)
                    )
                }

                item(key = "position") {
                    PositionCard(location = location, modifier = Modifier.staggeredEntry(2))
                }

                location.address?.let { address ->
                    item(key = "address") {
                        AddressCard(address = address, modifier = Modifier.staggeredEntry(3))
                    }
                }

                if (precise && location.satellites.isNotEmpty()) {
                    item(key = "satellitesInViewTitle", span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .staggeredEntry(4)
                                .padding(top = 8.dp)
                        ) {
                            SectionTitle(
                                title = R.string.location_section_satellites_in_view,
                                icon = Icons.Outlined.Satellite,
                                accentColor = AntarGreen
                            )
                        }
                    }

                    val rows = location.satellites
                        .sortedWith(compareByDescending<Satellite> { it.usedInFix }.thenByDescending { it.cn0DbHz })
                        .distinctBy { it.rowKey() }
                    itemsIndexed(rows, key = { _, satellite -> satellite.rowKey() }) { index, satellite ->
                        SatelliteRow(
                            satellite = satellite,
                            modifier = Modifier
                                .animateItem()
                                .staggeredEntry(5 + index)
                        )
                    }
                }
            }
        }
    }
}

/** Both are always requested together: Android 12+ ignores a FINE-only request. */
private val LOCATION_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

internal val LOCATION_PRIMING_POINTS = listOf(
    R.string.location_priming_point_purpose,
    R.string.location_priming_point_local,
    R.string.location_priming_point_private,
    R.string.location_priming_point_revoke
)

/** Stable per signal: the same satellite can be tracked on two bands (L1 and L5). */
private fun Satellite.rowKey(): String = "${constellation.name}-$svid-${carrierFrequencyHz ?: 0f}"
