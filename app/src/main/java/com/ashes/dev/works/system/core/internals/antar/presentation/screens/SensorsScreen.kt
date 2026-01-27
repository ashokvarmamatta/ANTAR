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
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SensorsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SensorsScreen(viewModel: SensorsViewModel = koinViewModel()) {
    val sensors = viewModel.getSensors()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Header Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = sensors.sensorCountMessage,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Sensor Item Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sensor Details",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // This should be a list of sensors, but for now we are just displaying one.
                    InfoRow("Sensor Type Name", sensors.sensorTypeName)
                    InfoRow("Name", sensors.name)
                    InfoRow("Vendor", sensors.vendor)
                    InfoRow("Wake Up Sensor", sensors.wakeUpSensor)
                    InfoRow("Power", sensors.power)
                }
            }
        }
    }
}
