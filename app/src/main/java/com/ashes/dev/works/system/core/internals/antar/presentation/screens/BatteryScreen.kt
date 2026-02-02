package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
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
    val capacityHistory by viewModel.capacityHistory.collectAsState()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                batteryInfo?.let {
                    BatteryHealthIndicator(batteryLevel = it.preciseLevel)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        batteryInfo?.let {
            CapacityHistoryCard(
                remainingCapacity = it.remainingCapacity,
                estimatedMax = it.estimatedMaxCapacity,
                isCharging = it.isCharging,
                history = capacityHistory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        batteryInfo?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Battery Info",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    InfoRow("Health", it.health)
                    InfoRow("Battery Health", it.batteryHealthStatus)
                    InfoRow("Temperature", "${it.temperature / 10.0}°C")
                    InfoRow("Charger Type", it.chargerType)
                    InfoRow("Technology", it.technology)
                    InfoRow("Voltage", "${it.voltage} V")
                    InfoRow("Design Capacity", "${it.designCapacity} mAh")
                    InfoRow("Estimated Max Capacity", "${it.estimatedMaxCapacity} mAh")
                    InfoRow("Remaining Capacity", "${it.remainingCapacity} mAh")
                    InfoRow("Charge Cycles", "${it.chargeCycles}")
                    InfoRow("Current", "${it.current / 1000} mA")
                    InfoRow("Power", String.format("%.2f W", it.power))
                    InfoRow("Dual-cell device", "No")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CapacityHistoryCard(
    remainingCapacity: Int,
    estimatedMax: Int,
    isCharging: Boolean,
    history: List<Int>
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = if (isCharging) "Charging" else "Discharging",
                        color = if (isCharging) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$remainingCapacity mAh",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Battery",
                    tint = if (isCharging) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                if (history.isNotEmpty()) {
                    val minHistory = history.min().toFloat()
                    val maxHistory = history.max().toFloat()
                    val dataRange = (maxHistory - minHistory).coerceAtLeast(10f)
                    
                    LineGraph(
                        dataPoints = history,
                        lineColor = if (isCharging) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.fillMaxSize(),
                        fixedMin = minHistory - (dataRange * 0.1f),
                        fixedMax = maxHistory + (dataRange * 0.1f)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Collecting data...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                if (history.isNotEmpty()) {
                    Text(text = "${history.first()} mAh", color = labelColor, fontSize = 11.sp)
                    Text(text = "${history.last()} mAh", color = labelColor, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun LineGraph(
    dataPoints: List<Number>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    fixedMin: Float? = null,
    fixedMax: Float? = null,
    showBezier: Boolean = true
) {
    if (dataPoints.isEmpty()) return

    val floatData = dataPoints.map { it.toFloat() }
    val maxVal = fixedMax ?: (floatData.maxOrNull() ?: 1f)
    val minVal = fixedMin ?: (floatData.minOrNull() ?: 0f)
    
    val animatedMax by animateFloatAsState(targetValue = maxVal, animationSpec = tween(500), label = "max")
    val animatedMin by animateFloatAsState(targetValue = minVal, animationSpec = tween(500), label = "min")
    
    val range = (animatedMax - animatedMin).coerceAtLeast(0.0001f)

    Canvas(modifier = modifier) {
        clipRect {
            if (floatData.size < 2) {
                val value = floatData[0]
                val normalized = ((value - animatedMin) / range).coerceIn(0f, 1f)
                val y = size.height - normalized * size.height
                drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(0f, y))
                return@clipRect
            }

            val widthPerPoint = size.width / (floatData.size - 1)
            val path = Path()
            val fillPath = Path()
            
            val getYPos = { valIndex: Int ->
                val value = floatData[valIndex]
                val normalized = ((value - animatedMin) / range).coerceIn(0f, 1f)
                size.height - normalized * size.height
            }

            val firstY = getYPos(0)
            path.moveTo(0f, firstY)
            fillPath.moveTo(0f, size.height)
            fillPath.lineTo(0f, firstY)

            for (i in 0 until floatData.size - 1) {
                val x1 = i * widthPerPoint
                val y1 = getYPos(i)
                val x2 = (i + 1) * widthPerPoint
                val y2 = getYPos(i + 1)

                if (showBezier) {
                    val cx1 = x1 + (x2 - x1) / 2f
                    path.cubicTo(cx1, y1, cx1, y2, x2, y2)
                    fillPath.cubicTo(cx1, y1, cx1, y2, x2, y2)
                } else {
                    path.lineTo(x2, y2)
                    fillPath.lineTo(x2, y2)
                }
            }
            
            fillPath.lineTo(size.width, size.height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = size.height
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            val lastX = size.width
            val lastY = getYPos(floatData.size - 1)
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = lineColor.copy(alpha = 0.4f),
                radius = 8.dp.toPx(),
                center = Offset(lastX, lastY)
            )
        }
    }
}

@Composable
fun BatteryHealthIndicator(
    batteryLevel: Double,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(targetValue = batteryLevel.toFloat(), label = "lvl")
    val color = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(180.dp)
            .graphicsLayer()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 15.dp.toPx()
            
            // Background track
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Animated "Wave" Progress
            val path = Path()
            val waveCount = 24
            val waveAmplitude = 8f
            val sweepAngle = (animatedLevel / 100f * 360f).coerceIn(0f, 360f)
            
            for (angle in 0..sweepAngle.toInt() step 1) {
                val theta = Math.toRadians(angle.toDouble() - 90.0)
                val r = radius + waveAmplitude * sin(waveCount * Math.toRadians(angle.toDouble())).toFloat()
                val x = center.x + r * cos(theta).toFloat()
                val y = center.y + r * sin(theta).toFloat()
                if (angle == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.1f), Color.Transparent),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.2f", animatedLevel),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "%",
                    fontSize = 18.sp,
                    color = color,
                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                )
            }
        }
    }
}
