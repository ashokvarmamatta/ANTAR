package com.ashes.dev.works.system.core.internals.antar.presentation.network

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Wifi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarTeal
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.ui.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.core.ui.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.SimInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

/**
 * Android only reveals the connected network's SSID, BSSID and security to apps holding precise
 * location. COARSE is requested with FINE because Android 12+ ignores a FINE-only request.
 */
private val WIFI_IDENTITY_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

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

@Composable
private fun NetworkHeader(connection: ActiveConnection, wifiLink: WifiConnection?, modifier: Modifier = Modifier) {
    val typeLabel = stringResource(connection.type.labelRes())
    val frequency = wifiLink?.frequencyMhz?.let { stringResource(R.string.network_value_mhz, it) }

    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = connection.type.icon(),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = AntarBlue
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.network_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                connection.ipv4Address?.let { ip ->
                    Text(text = ip, style = MaterialTheme.typography.bodyMedium, color = AntarCyan)
                }
                Text(
                    text = if (frequency != null) {
                        stringResource(R.string.network_value_type_and_frequency, typeLabel, frequency)
                    } else {
                        typeLabel
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }
        }
    }
}

@Composable
private fun ConnectionCard(connection: ActiveConnection, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_connection, icon = Icons.Outlined.Public)
        InfoRow(R.string.network_label_type, stringResource(connection.type.labelRes()))
        InfoRow(R.string.network_label_ip, connection.ipv4Address)
        InfoRow(
            R.string.network_label_ipv6,
            connection.ipv6Addresses.takeIf { it.isNotEmpty() }?.joinToString(separator = "\n"),
            singleLine = false
        )
        InfoRow(R.string.network_label_netmask, connection.netmask)
        InfoRow(R.string.network_label_gateway, connection.gateway)
        InfoRow(R.string.network_label_dhcp_server, connection.dhcpServer)
        InfoRow(
            R.string.network_label_dhcp_lease,
            connection.dhcpLeaseSeconds?.let { pluralStringResource(R.plurals.network_value_lease_seconds, it, it) }
        )
        connection.dnsServers.forEachIndexed { index, server ->
            InfoRow(label = stringResource(R.string.network_label_dns, index + 1), value = server)
        }
        InfoRow(R.string.network_label_interface, connection.interfaceName)
    }
}

@Composable
private fun WifiCard(wifi: WifiState, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_wifi, icon = Icons.Outlined.Wifi, accentColor = AntarCyan)
        InfoRow(
            R.string.network_label_status,
            stringResource(if (wifi.isEnabled) R.string.common_enabled else R.string.common_disabled)
        )
        val link = wifi.connection
        if (link == null) {
            InfoRow(R.string.network_label_wifi_connection, stringResource(R.string.network_wifi_not_connected))
        } else {
            InfoRow(R.string.network_label_signal, link.rssiDbm?.let { stringResource(R.string.network_value_dbm, it) })
            InfoRow(R.string.network_label_link_speed, link.linkSpeedMbps?.let { stringResource(R.string.network_value_mbps, it) })
            InfoRow(R.string.network_label_frequency, link.frequencyMhz?.let { stringResource(R.string.network_value_mhz, it) })
            InfoRow(R.string.network_label_wifi_standard, link.standard?.let { stringResource(it.labelRes()) })
        }
    }
}

@Composable
private fun WifiIdentityCard(
    link: WifiConnection,
    accessGranted: Boolean,
    permanentlyDenied: Boolean,
    onRequestAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_wifi_identity, icon = Icons.Outlined.Security, accentColor = AntarTeal)
        if (accessGranted) {
            InfoRow(R.string.network_label_ssid, link.ssid)
            InfoRow(R.string.network_label_bssid, link.bssid)
            InfoRow(R.string.network_label_security, link.security?.let { stringResource(it.labelRes()) })
            if (link.ssid == null && link.bssid == null) {
                Text(
                    text = stringResource(R.string.network_wifi_access_location_off),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            WifiAccessPrompt(permanentlyDenied = permanentlyDenied, onRequestAccess = onRequestAccess)
        }
    }
}

/**
 * Inline stand-in for PermissionGate inside a card (the gate is a full-screen scrolling layout and
 * cannot sit in a lazy grid item). Same flow: explanation, in-app priming dialog before the system
 * dialog, and "Open settings" once Android will no longer ask.
 */
@Composable
private fun WifiAccessPrompt(permanentlyDenied: Boolean, onRequestAccess: () -> Unit) {
    var showPriming by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Text(
        text = stringResource(if (permanentlyDenied) R.string.permission_denied_body else R.string.network_wifi_access_body),
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
            text = stringResource(if (permanentlyDenied) R.string.permission_open_settings else R.string.permission_grant),
            fontWeight = FontWeight.Bold
        )
    }

    if (showPriming) {
        PermissionPrimingDialog(
            icon = Icons.Outlined.Security,
            title = R.string.network_wifi_priming_title,
            points = listOf(
                R.string.network_wifi_priming_point_purpose,
                R.string.network_wifi_priming_point_local,
                R.string.network_wifi_priming_point_private,
                R.string.network_wifi_priming_point_optional
            ),
            onAllow = {
                showPriming = false
                onRequestAccess()
            },
            onDismiss = { showPriming = false }
        )
    }
}

@Composable
private fun MobileDataCard(telephony: TelephonyInfo, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_mobile_data, icon = Icons.Outlined.CellTower, accentColor = AntarPurple)
        InfoRow(
            R.string.network_label_status,
            telephony.isMobileDataConnected?.let { connected ->
                stringResource(if (connected) R.string.network_data_connected else R.string.network_data_disconnected)
            }
        )
        InfoRow(R.string.network_label_sim_slots, telephony.modemCount.toString())
        InfoRow(R.string.network_label_phone_type, stringResource(telephony.phoneType.labelRes()))
    }
}

@Composable
private fun SimInfoCard(sim: SimInfo, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_sim, icon = Icons.Outlined.SimCard, accentColor = AntarGreen)
        InfoRow(R.string.network_label_sim_name, sim.operatorName ?: stringResource(R.string.network_sim_detected))
        InfoRow(R.string.network_label_country_iso, sim.countryIso)
        InfoRow(R.string.network_label_mcc, sim.mcc)
        InfoRow(R.string.network_label_mnc, sim.mnc)
        InfoRow(R.string.network_label_carrier_id, sim.carrierId?.toString())
        InfoRow(R.string.network_label_carrier_name, sim.networkOperatorName)
        InfoRow(
            R.string.network_label_roaming,
            stringResource(if (sim.isRoaming) R.string.common_yes else R.string.common_no)
        )
    }
}

private fun openAppSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
