package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.NetworkViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NetworkScreen(viewModel: NetworkViewModel = koinViewModel()) {
    val context = LocalContext.current
    val network = viewModel.getNetwork()

    val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // Handle permissions result
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions.launch(permissionsToRequest.toTypedArray())
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
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
}
