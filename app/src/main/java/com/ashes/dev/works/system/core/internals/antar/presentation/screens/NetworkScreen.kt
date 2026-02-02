package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.Network
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

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item(key = "header") {
                NetworkHeader(network = network)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // WIFI Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WIFI",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
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
                        InfoRow("interface", network.wifiInterface)
                        InfoRow("Link speed", network.linkSpeed)
                        InfoRow("Frequency", network.frequency)
                        InfoRow("WiFi features", network.wifiFeatures, singleLine = false)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // Mobile data Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mobile data",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        InfoRow("Status", network.mobileDataStatus)
                        InfoRow("Multi SIM", network.multiSim)
                        InfoRow("Device type", network.deviceType)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // SIM 1 Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SIM 1",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
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

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // SIM 2 Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SIM 2",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
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
            Text("Network and phone permissions are required to use this feature.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { networkPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
private fun NetworkHeader(network: Network) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_signal),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF1976D2)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                HeaderItem(icon = Icons.Default.Public, text = "Wi-Fi")
                HeaderItem(icon = Icons.Default.Info, text = network.ip)
                HeaderItem(icon = Icons.Default.Build, text = network.frequency)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HeaderButton(icon = Icons.Default.Refresh, text = "Usage")
                HeaderButton(icon = Icons.Default.Public, text = "Public IP")
            }
        }
    }
}

@Composable
private fun HeaderItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun HeaderButton(icon: ImageVector, text: String) {
    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
