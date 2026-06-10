package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
        buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
        }
    )
    var showNetworkDisclosure by remember { mutableStateOf(false) }

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
                "Location and nearby-Wi-Fi access are needed to read your current Wi-Fi security type",
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showNetworkDisclosure = true },
                colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
            ) {
                Text("Grant Permissions", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showNetworkDisclosure) {
        AlertDialog(
            onDismissRequest = { showNetworkDisclosure = false },
            title = { Text("Network permissions") },
            text = {
                Text(
                    "The Network screen needs permission to read the security type of the Wi-Fi you're currently connected to.\n\n" +
                        "Location: Android exposes Wi-Fi scan results only to apps holding location.\n\n" +
                        "Nearby Wi-Fi devices: required by Android 13+ to scan Wi-Fi networks. ANTAR uses this only to identify the network you're already connected to; it is not used to derive your location.\n\n" +
                        "These permissions are used solely to display WPA2, WPA3, WEP, Open, and similar Wi-Fi security labels on the Network screen. Nothing is transmitted, stored, or shared."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNetworkDisclosure = false
                        networkPermissionsState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNetworkDisclosure = false }) {
                    Text("Not now")
                }
            }
        )
    }
}
