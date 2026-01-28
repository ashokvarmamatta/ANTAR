package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val dashboard by viewModel.dashboardInfo.collectAsState()

    dashboard?.let {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                // Header Summaries
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Device Model",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = it.deviceModel,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "OS Version",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = it.osVersion,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // RAM Gauge
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "RAM Usage", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        RamGauge(percentage = it.ramUsagePercentage.replace("%", "").toFloatOrNull()?.div(100) ?: 0f)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "${it.usedMemory} / ${it.totalMemory}")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // Quick Summaries
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Quick Summaries", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Internal Storage",
                            style = MaterialTheme.typography.titleMedium
                        )
                        LinearProgressIndicator(
                            progress = it.internalStorageUsage.replace("%", "").toFloatOrNull()?.div(100) ?: 0f,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                        Text(
                            text = it.storageAnalysisMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoRow(label = "Battery", value = "${it.batteryStatus}, ${it.batteryVoltage}, ${it.batteryTemp}")
                        InfoRow(label = "Sensors", value = "${it.sensorCount} available")
                        InfoRow(label = "Apps", value = "${it.appCount} installed")
                    }
                }
            }
        }
    }
}

@Composable
fun RamGauge(percentage: Float) {
    val animatedPercentage by animateFloatAsState(targetValue = percentage, label = "ram_gauge")
    val stroke = Stroke(width = 20f, cap = StrokeCap.Round)
    Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(
                color = Color.LightGray,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = Color.Green,
                startAngle = -215f,
                sweepAngle = 250f * animatedPercentage,
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
