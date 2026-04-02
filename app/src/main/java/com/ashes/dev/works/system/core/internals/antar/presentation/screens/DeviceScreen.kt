package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DeviceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = koinViewModel()) {
    val device by viewModel.device.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GradientHeaderCard {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "DEVICE",
                        style = MaterialTheme.typography.labelMedium,
                        color = AntarCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${device.manufacturer} \u2022 ${device.model}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AntarGray
                    )
                }
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "General Info", icon = Icons.Outlined.PhoneAndroid)
                InfoRow("Device name", device.deviceName)
                InfoRow("Model", device.model)
                InfoRow("Manufacturer", device.manufacturer)
                InfoRow("Device", device.device)
                InfoRow("Board", device.board)
                InfoRow("Hardware", device.hardware)
                InfoRow("Brand", device.brand)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Identifiers & Connectivity", icon = Icons.Outlined.Fingerprint, accentColor = AntarPurple)
                CopyableInfoRow("Google Advertising ID", device.googleAdvertisingId)
                CopyableInfoRow("Android Device ID", device.androidDeviceId)
                CopyableInfoRow("Hardware Serial", device.hardwareSerial)
                CopyableInfoRow("Build Fingerprint", device.buildFingerprint)
                InfoRow("Device type", device.deviceType)
                InfoRow("Network operator", device.networkOperator)
                InfoRow("Network Type", device.networkType)
                InfoRow("WiFi MAC address", device.wifiMacAddress)
                InfoRow("Bluetooth MAC address", device.bluetoothMacAddress)
                InfoRow("USB debugging", device.usbDebugging)
                InfoRow("Supports 6G", device.supports6G)
            }
        }
    }
}
