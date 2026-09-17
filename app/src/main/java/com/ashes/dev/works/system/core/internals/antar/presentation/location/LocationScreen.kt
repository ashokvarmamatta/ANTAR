package com.ashes.dev.works.system.core.internals.antar.presentation.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.ui.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.core.ui.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.core.ui.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.domain.model.GnssConstellation
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

/** Both are always requested together: Android 12+ ignores a FINE-only request. */
private val LOCATION_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

private val LOCATION_PRIMING_POINTS = listOf(
    R.string.location_priming_point_purpose,
    R.string.location_priming_point_local,
    R.string.location_priming_point_private,
    R.string.location_priming_point_revoke
)

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

/** Stable per signal: the same satellite can be tracked on two bands (L1 and L5). */
private fun Satellite.rowKey(): String = "${constellation.name}-$svid-${carrierFrequencyHz ?: 0f}"

@Composable
private fun GpsDisabledBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AntarRed.copy(alpha = 0.15f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.location_gps_disabled),
            color = AntarRed,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LocationHeader(location: Location?, modifier: Modifier = Modifier) {
    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = AntarRed
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                if (location == null) {
                    Text(
                        text = stringResource(R.string.location_waiting_for_fix),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    FixSummary(location)
                }
            }
        }
    }
}

@Composable
private fun FixSummary(location: Location) {
    Text(
        text = stringResource(R.string.location_value_coordinates, location.latitude, location.longitude),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    val inView = location.satellites.size
    if (inView > 0) {
        Text(
            text = pluralStringResource(R.plurals.location_satellites_in_view_count, inView, inView),
            style = MaterialTheme.typography.bodySmall,
            color = AntarGreen
        )
    }
    location.address?.let { address ->
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = AntarGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SatellitesCard(
    satellites: List<Satellite>,
    precise: Boolean,
    permanentlyDenied: Boolean,
    onRequestPreciseAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.location_section_satellites, icon = Icons.Outlined.Satellite, accentColor = AntarGreen)
        if (!precise) {
            PreciseLocationPrompt(permanentlyDenied = permanentlyDenied, onRequestAccess = onRequestPreciseAccess)
        } else {
            val counts = satellites
                .filter { it.constellation != GnssConstellation.UNKNOWN }
                .groupingBy { it.constellation }
                .eachCount()
            GnssConstellation.entries
                .filter { it != GnssConstellation.UNKNOWN }
                .map { it to (counts[it] ?: 0) }
                .sortedByDescending { it.second }
                .forEach { (constellation, count) ->
                    InfoRow(constellation.labelRes(), count.toString())
                }
        }
    }
}

/**
 * Shown on approximate-only access. Same flow as PermissionGate: in-app priming before the system
 * dialog, and "Open settings" once Android will no longer ask.
 */
@Composable
private fun PreciseLocationPrompt(permanentlyDenied: Boolean, onRequestAccess: () -> Unit) {
    var showPriming by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Text(
        text = stringResource(R.string.location_precise_needed_title),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = stringResource(if (permanentlyDenied) R.string.permission_denied_body else R.string.location_precise_needed_body),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))
    val interactionSource = remember { MutableInteractionSource() }
    FilledTonalButton(
        onClick = {
            if (permanentlyDenied) {
                openAppSettings(context)
            } else {
                showPriming = true
            }
        },
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interactionSource)
    ) {
        Text(
            text = stringResource(if (permanentlyDenied) R.string.permission_open_settings else R.string.location_precise_action),
            fontWeight = FontWeight.Bold
        )
    }

    if (showPriming) {
        PermissionPrimingDialog(
            icon = Icons.Outlined.GpsFixed,
            title = R.string.location_priming_title,
            points = LOCATION_PRIMING_POINTS,
            onAllow = {
                showPriming = false
                onRequestAccess()
            },
            onDismiss = { showPriming = false }
        )
    }
}

@Composable
private fun PositionCard(location: Location, modifier: Modifier = Modifier) {
    val horizontal = location.horizontalAccuracyMeters?.let { stringResource(R.string.location_value_meters, it) }
    val vertical = location.verticalAccuracyMeters?.let { stringResource(R.string.location_value_meters, it) }
    val hdop = location.hdop?.let { stringResource(R.string.location_value_dop, it) }
    val vdop = location.vdop?.let { stringResource(R.string.location_value_dop, it) }
    val satellitesInView = location.satellites.size

    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.location_section_position, icon = Icons.Outlined.GpsFixed)
        InfoRow(R.string.location_label_latitude, stringResource(R.string.location_value_coordinate, location.latitude))
        InfoRow(R.string.location_label_longitude, stringResource(R.string.location_value_coordinate, location.longitude))
        InfoRow(R.string.location_label_altitude, location.altitudeMeters?.let { stringResource(R.string.location_value_meters, it) })
        InfoRow(R.string.location_label_sea_level_altitude, location.mslAltitudeMeters?.let { stringResource(R.string.location_value_meters, it) })
        InfoRow(R.string.location_label_speed, location.speedMetersPerSecond?.let { stringResource(R.string.location_value_speed, it) })
        InfoRow(
            R.string.location_label_speed_accuracy,
            location.speedAccuracyMetersPerSecond?.let { stringResource(R.string.location_value_speed, it) }
        )
        InfoRow(R.string.location_label_pdop, location.pdop?.let { stringResource(R.string.location_value_dop, it) })
        InfoRow(
            R.string.location_label_hv_dop,
            if (hdop != null && vdop != null) stringResource(R.string.location_value_horizontal_vertical, hdop, vdop) else null
        )
        InfoRow(
            R.string.location_label_hv_accuracy,
            when {
                horizontal != null && vertical != null ->
                    stringResource(R.string.location_value_horizontal_vertical, horizontal, vertical)
                else -> horizontal
            }
        )
        InfoRow(
            R.string.location_label_satellites_used,
            if (satellitesInView > 0) {
                stringResource(R.string.location_value_used_of_total, location.satellites.count { it.usedInFix }, satellitesInView)
            } else {
                null
            }
        )
        InfoRow(R.string.location_label_bearing, location.bearingDegrees?.let { stringResource(R.string.location_value_degrees, it) })
        InfoRow(
            R.string.location_label_bearing_accuracy,
            location.bearingAccuracyDegrees?.let { stringResource(R.string.location_value_degrees, it) }
        )
    }
}

@Composable
private fun AddressCard(address: String, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.location_section_address, icon = Icons.Outlined.Map, accentColor = AntarPurple)
        Text(
            text = address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SatelliteRow(satellite: Satellite, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (satellite.usedInFix) AntarGreen else AntarGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.location_satellite_title,
                    stringResource(satellite.constellation.labelRes()),
                    satellite.svid
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(
                    R.string.location_satellite_sky_position,
                    satellite.elevationDegrees,
                    satellite.azimuthDegrees
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.location_value_cn0, satellite.cn0DbHz),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AntarCyan
            )
            Text(
                text = stringResource(
                    if (satellite.usedInFix) R.string.location_satellite_used_in_fix else R.string.location_satellite_not_used
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun openAppSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
