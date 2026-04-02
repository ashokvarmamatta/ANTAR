package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorDetail
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SensorsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SensorsScreen(viewModel: SensorsViewModel = koinViewModel()) {
    val sensorsState by viewModel.sensorsState.collectAsState()

    if (sensorsState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AntarCyan)
        }
        return
    }

    val sensors = sensorsState!!

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            GradientHeaderCard {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarGreen
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Device Sensors",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sensors.sensorCountMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        items(sensors.sensorList, key = { "${it.name}_${it.type}" }) { sensor ->
            SensorItem(sensor)
        }
    }
}

@Composable
fun SensorItem(sensor: SensorDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AntarCard.copy(alpha = 0.5f))
            .border(0.5.dp, AntarDimGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = AntarCyan.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .border(0.5.dp, AntarCyan.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Sensors,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = AntarCyan
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = sensor.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabelValue("Vendor", sensor.vendor)
                    LabelValue("Power", "${sensor.power}mA")
                }
            }
        }
    }
}
