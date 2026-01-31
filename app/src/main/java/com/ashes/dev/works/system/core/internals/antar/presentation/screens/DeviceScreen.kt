package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DeviceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DeviceScreen(viewModel: DeviceViewModel = koinViewModel()) {
    val device by viewModel.device.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item(key = "header") {
            DeviceHeader(deviceName = device.deviceName)
        }

        item(key = "general_info") { 
            Spacer(modifier = Modifier.height(16.dp))
            GeneralInfoCard(
                deviceName = device.deviceName,
                model = device.model,
                manufacturer = device.manufacturer,
                device = device.device,
                board = device.board,
                hardware = device.hardware,
                brand = device.brand
            )
        }

        item(key = "identifiers") { 
            Spacer(modifier = Modifier.height(16.dp))
            IdentifiersAndConnectivityCard(
                googleAdvertisingId = device.googleAdvertisingId,
                androidDeviceId = device.androidDeviceId,
                hardwareSerial = device.hardwareSerial,
                buildFingerprint = device.buildFingerprint,
                deviceType = device.deviceType,
                networkOperator = device.networkOperator,
                networkType = device.networkType,
                wifiMacAddress = device.wifiMacAddress,
                bluetoothMacAddress = device.bluetoothMacAddress,
                usbDebugging = device.usbDebugging
            )
        }
    }
}

@Composable
private fun DeviceHeader(deviceName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = deviceName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GeneralInfoCard(
    deviceName: String,
    model: String,
    manufacturer: String,
    device: String,
    board: String,
    hardware: String,
    brand: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "General Info",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("Device name", deviceName)
            InfoRow("Model", model)
            InfoRow("Manufacturer", manufacturer)
            InfoRow("Device", device)
            InfoRow("Board", board)
            InfoRow("Hardware", hardware)
            InfoRow("Brand", brand)
        }
    }
}

@Composable
private fun IdentifiersAndConnectivityCard(
    googleAdvertisingId: String,
    androidDeviceId: String,
    hardwareSerial: String,
    buildFingerprint: String,
    deviceType: String,
    networkOperator: String,
    networkType: String,
    wifiMacAddress: String,
    bluetoothMacAddress: String,
    usbDebugging: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Identifiers & Connectivity",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("Google Advertising ID", googleAdvertisingId)
            InfoRow("Android Device ID", androidDeviceId)
            InfoRow("Hardware serial", hardwareSerial)
            InfoRow("Build fingerprint", buildFingerprint, singleLine = false)
            InfoRow("Device type", deviceType)
            InfoRow("Network operator", networkOperator)
            InfoRow("Network Type", networkType)
            InfoRow("WiFi MAC address", wifiMacAddress)
            InfoRow("Bluetooth MAC address", bluetoothMacAddress)
            InfoRow("USB debugging", usbDebugging)
        }
    }
}
