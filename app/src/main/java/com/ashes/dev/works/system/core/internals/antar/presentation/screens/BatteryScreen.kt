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
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = koinViewModel()) {
    val battery = viewModel.getBattery()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Header Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Battery Level: ${battery.batteryLevel}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Status: ${battery.status}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Battery Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Battery Info",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Health", battery.health)
                    InfoRow("Battery Health", battery.batteryHealthStatus)
                    InfoRow("Temperature", battery.temperature)
                    InfoRow("Charger Type", battery.powerSource)
                    InfoRow("Technology", battery.technology)
                    InfoRow("Voltage", battery.voltage)
                    InfoRow("Design Capacity", battery.designCapacity)
                    InfoRow("Estimated Max Capacity", battery.estimatedMaxCapacity)
                    InfoRow("Drained Capacity", battery.drainedCapacity)
                    InfoRow("Remaining Capacity", battery.remainingCapacity)
                    InfoRow("Charge Cycles", battery.chargeCycles)
                    InfoRow("Current", battery.current)
                    InfoRow("Power", battery.power)
                    InfoRow("Dual-cell device", battery.dualCellDevice)
                }
            }
        }
    }
}
