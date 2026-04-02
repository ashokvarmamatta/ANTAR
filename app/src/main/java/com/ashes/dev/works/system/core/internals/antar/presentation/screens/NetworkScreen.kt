package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.NetworkViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NetworkScreen(viewModel: NetworkViewModel = koinViewModel()) {
    val networkPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    if (networkPermissionsState.allPermissionsGranted) {
        val network = viewModel.getNetwork()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GradientHeaderCard {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Wifi,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = AntarBlue
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Network",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (network.ip.isNotBlank() && network.ip != "- - -") {
                                Text(
                                    text = network.ip,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AntarCyan
                                )
                            }
                            if (network.frequency.isNotBlank() && network.frequency != "- - -") {
                                Text(
                                    text = network.frequency,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AntarGray
                                )
                            }
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "WiFi", icon = Icons.Outlined.Wifi)
                    InfoRow("Status", network.wifiStatus)
                    InfoRow("Safety", network.wifiSafety)
                    InfoRow("BSSID", network.bssid)
                    InfoRow("DHCP", network.dhcp)
                    InfoRow("DHCP lease duration", network.dhcpLeaseDuration)
                    InfoRow("Gateway", network.gateway)
                    InfoRow("Netmask", network.netmask)
                    InfoRow("DNS1", network.dns1)
                    InfoRow("DNS2", network.dns2)
                    InfoRow("IP", network.ip)
                    InfoRow("IPv6", network.ipv6)
                    InfoRow("Interface", network.wifiInterface)
                    InfoRow("Link speed", network.linkSpeed)
                    InfoRow("Frequency", network.frequency)
                    InfoRow("WiFi features", network.wifiFeatures, singleLine = false)
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Mobile Data", icon = Icons.Outlined.CellTower, accentColor = AntarPurple)
                    InfoRow("Status", network.mobileDataStatus)
                    InfoRow("Multi SIM", network.multiSim)
                    InfoRow("Device type", network.deviceType)
                }
            }

            // SIM 1 — only show if has data
            if (network.sim1Name.isNotBlank() && network.sim1Name != "- - -") {
                item {
                    PremiumCard {
                        SectionTitle(title = "SIM 1", icon = Icons.Outlined.SimCard, accentColor = AntarGreen)
                        InfoRow("Name", network.sim1Name)
                        InfoRow("Phone number", network.sim1PhoneNumber)
                        InfoRow("Country ISO", network.sim1CountryIso)
                        InfoRow("MCC", network.sim1Mcc)
                        InfoRow("MNC", network.sim1Mnc)
                        InfoRow("Carrier id", network.sim1CarrierId)
                        InfoRow("Carrier name", network.sim1CarrierName)
                        InfoRow("Data roaming", network.sim1DataRoaming)
                    }
                }
            }

            // SIM 2 — only show if has data
            if (network.sim2Name.isNotBlank() && network.sim2Name != "- - -") {
                item {
                    PremiumCard {
                        SectionTitle(title = "SIM 2", icon = Icons.Outlined.SimCard, accentColor = AntarBlue)
                        InfoRow("Name", network.sim2Name)
                        InfoRow("Phone number", network.sim2PhoneNumber)
                        InfoRow("Country ISO", network.sim2CountryIso)
                        InfoRow("MCC", network.sim2Mcc)
                        InfoRow("MNC", network.sim2Mnc)
                        InfoRow("Carrier id", network.sim2CarrierId)
                        InfoRow("Carrier name", network.sim2CarrierName)
                        InfoRow("Data roaming", network.sim2DataRoaming)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Wifi,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = AntarGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Network permissions required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Phone and location access needed for network details",
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { networkPermissionsState.launchMultiplePermissionRequest() },
                colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
            ) {
                Text("Grant Permissions", fontWeight = FontWeight.Bold)
            }
        }
    }
}
