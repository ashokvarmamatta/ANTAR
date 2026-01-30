package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val dashboard by viewModel.dashboardInfo.collectAsState()

    dashboard?.let {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                // Header Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Chip(text = it.deviceModel)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Chip(text = "Android ${it.osVersion}")
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // RAM Utilization
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "RAM Utilization", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            RamGauge(percentage = it.ramUsagePercentage.toFloat() / 100)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "${it.usedMemory} / ${it.totalMemory}")
                            Text(text = "Status: ${it.ramStatus}", color = Color.Green)
                        }
                        Icon(Icons.Default.List, contentDescription = null)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // Internal Storage
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${it.internalStoragePercentage}% Full", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "INTERNAL STORAGE", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Storage, contentDescription = "Internal Storage")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${it.usedStorage} / ${it.totalStorage} used")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = it.internalStoragePercentage.toFloat() / 100,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // Battery
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        BatteryIcon(isCharging = it.batteryStatus == "Charging")
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Column {
                            Text(text = "POWER SOURCE", style = MaterialTheme.typography.labelSmall)
                            Text(text = it.batteryStatus, style = MaterialTheme.typography.titleLarge)
                            Text(text = "${it.batteryTemp} • ${it.batteryVoltage}")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        SmallInfoCard(
                            title = "PROCESSOR",
                            value = it.processorName,
                            subtitle = it.processorDetails,
                            icon = Icons.Default.Memory
                        )
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        SmallInfoCard(
                            title = "SENSORS",
                            value = "${it.sensorCount} Available",
                            subtitle = "",
                            icon = Icons.Default.Sensors
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        SmallInfoCard(
                            title = "APPLICATIONS",
                            value = "${it.appCount} Installed",
                            subtitle = "12 System Updates",
                            icon = Icons.Default.Apps
                        )
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        SmallInfoCard(
                            title = "SYS HEALTH",
                            value = it.sysHealth,
                            subtitle = "Uptime: ${it.uptime}",
                            icon = Icons.Default.Verified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(text = text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
fun RamGauge(percentage: Float) {
    val animatedPercentage by animateFloatAsState(targetValue = percentage, label = "ram_gauge")
    val stroke = Stroke(width = 16f, cap = StrokeCap.Round)
    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) {
            drawArc(
                color = Color(0xFF383838),
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = Color(0xFF00C853),
                startAngle = -215f,
                sweepAngle = 250f * animatedPercentage,
                useCenter = false,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "ACTIVE", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SmallInfoCard(title: String, value: String, subtitle: String, icon: ImageVector) {
    Card(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BatteryIcon(isCharging: Boolean) {
    val color = if (isCharging) Color.Green else Color.White
    Canvas(modifier = Modifier.size(width = 36.dp, height = 48.dp)) {        val strokeWidth = 4f
        val cornerRadius = 4f

        // Battery outline
        drawRoundRect(
            color = color,
            size = size,
            style = Stroke(width = strokeWidth),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // Battery level
        val levelHeight = size.height * 0.6f
        val levelWidth = size.width - (strokeWidth * 2) - 4.dp.toPx()
        val levelTop = size.height - levelHeight - strokeWidth - 2.dp.toPx()
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(strokeWidth + 2.dp.toPx(), levelTop),
            size = androidx.compose.ui.geometry.Size(levelWidth, levelHeight)
        )

        // Battery terminal
        val terminalWidth = size.width * 0.4f
        val terminalHeight = strokeWidth
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(size.width / 2 - terminalWidth / 2, 0f),
            size = androidx.compose.ui.geometry.Size(terminalWidth, terminalHeight)
        )
    }
}
