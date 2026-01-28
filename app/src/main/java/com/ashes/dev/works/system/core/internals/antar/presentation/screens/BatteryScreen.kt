package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = koinViewModel()) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val history by viewModel.currentHistory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark Background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            batteryInfo?.let {
                BatteryHealthIndicator(batteryLevel = it.batteryLevel)

                Spacer(modifier = Modifier.height(24.dp))

                if (it.isCharging) {
                    ChargingGraphCard(
                        currentNow = it.current,
                        power = it.power,
                        temp = it.temperature,
                        history = history,
                        chargerType = it.chargerType
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            batteryInfo?.let {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Battery Info",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        InfoRow("Health", it.health)
                        InfoRow("Battery Health", it.batteryHealthStatus)
                        InfoRow("Temperature", "${it.temperature / 10f}°C")
                        InfoRow("Charger Type", it.chargerType)
                        InfoRow("Technology", it.technology)
                        InfoRow("Voltage", "${it.voltage} V")
                        InfoRow("Design Capacity", "${it.designCapacity} mAh")
                        InfoRow("Estimated Max Capacity", "${it.estimatedMaxCapacity} mAh")
                        InfoRow("Remaining Capacity", "${it.remainingCapacity} mAh")
                        InfoRow("Charge Cycles", "${it.chargeCycles}")
                        InfoRow("Current", "${it.current} μA")
                        InfoRow("Power", String.format("%.2f W", it.power))
                        InfoRow("Dual-cell device", "No")
                    }
                }
            }
        }
    }
}

@Composable
fun ChargingGraphCard(
    currentNow: Int,
    power: Double,
    temp: Int,
    history: List<Int>,
    chargerType: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9)), // Light Blue
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            // Top Row: Current and Temp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Current : $currentNow mA", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "$temp °C", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Middle: Battery Icon + Graph
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Large Battery Icon
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Battery",
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // The Live Graph
                LineGraph(
                    dataPoints = history,
                    lineColor = Color(0xFF0D47A1),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Row: Power and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Power : $power W", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = chargerType, color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun LineGraph(
    dataPoints: List<Int>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    Canvas(modifier = modifier) {
        val path = Path()
        val maxVal = dataPoints.maxOrNull()?.toFloat() ?: 1f
        val minVal = dataPoints.minOrNull()?.toFloat() ?: 0f
        val range = maxVal - minVal

        val widthPerPoint = size.width / (dataPoints.size - 1)

        dataPoints.forEachIndexed { index, value ->
            val x = index * widthPerPoint
            // Normalize height to fit canvas
            val normalizedY = ((value - minVal) / range) * size.height
            val y = size.height - normalizedY

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BatteryHealthIndicator(
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(150.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 10.dp.toPx() // Padding
            val waveCount = 20
            val waveAmplitude = 10f // How "deep" the waves are

            val path = Path()

            // Draw the Wavy Circle
            for (angle in 0..360 step 1) {
                val theta = Math.toRadians(angle.toDouble())
                // r = R + A * sin(n * theta)
                val r = radius + waveAmplitude * sin(waveCount * theta).toFloat()

                val x = center.x + r * cos(theta).toFloat()
                val y = center.y + r * sin(theta).toFloat()

                if (angle == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = Color(0xFF0D47A1), // Dark Blue
                style = Stroke(width = 6.dp.toPx())
            )

            // Draw the small progress arc at the bottom (gray line in your image)
            drawArc(
                color = Color.Gray.copy(alpha = 0.5f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }

        // Text inside
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$batteryLevel",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )
            Text(
                text = "%",
                fontSize = 20.sp,
                color = Color(0xFF0D47A1),
                modifier = Modifier.offset(x = 24.dp, y = (-24).dp) // Align % like the image
            )
        }
    }
}
