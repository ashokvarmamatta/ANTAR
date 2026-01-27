package com.ashes.dev.works.system.core.internals.antar.presentation.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DeviceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = koinViewModel()) {
    val device = viewModel.getDevice()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Header Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // General Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "General Info",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Device name", device.deviceName)
                    InfoRow("Model", device.model)
                    InfoRow("Manufacturer", device.manufacturer)
                    InfoRow("Device", device.device)
                    InfoRow("Board", device.board)
                    InfoRow("Hardware", device.hardware)
                    InfoRow("Brand", device.brand)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Identifiers & Connectivity Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                     Text(
                        text = "Identifiers & Connectivity",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Google Advertising ID", device.googleAdvertisingId)
                    InfoRow("Android Device ID", device.androidDeviceId)
                    InfoRow("Hardware serial", device.hardwareSerial)
                    InfoRow("Build fingerprint", device.buildFingerprint, singleLine = false)
                    InfoRow("Device type", device.deviceType)
                    InfoRow("Network operator", device.networkOperator)
                    InfoRow("Network Type", device.networkType)
                    InfoRow("WiFi MAC address", device.wifiMacAddress)
                    InfoRow("Bluetooth MAC address", device.bluetoothMacAddress)
                    InfoRow("USB debugging", device.usbDebugging)
                }
            }
        }
    }
}
