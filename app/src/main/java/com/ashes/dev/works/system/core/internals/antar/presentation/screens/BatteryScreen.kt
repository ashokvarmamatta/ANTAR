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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryMetric
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = koinViewModel()) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val currentHistory by viewModel.currentHistory.collectAsState()
    val powerHistory by viewModel.powerHistory.collectAsState()
    val tempHistory by viewModel.tempHistory.collectAsState()
    val selectedMetric by viewModel.selectedMetric.collectAsState()
    val showMetricGraph by viewModel.showMetricGraph.collectAsState()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            IconButton(
                onClick = { viewModel.toggleMetricGraph() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = if (showMetricGraph) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Metric Graph",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                batteryInfo?.let {
                    BatteryHealthIndicator(batteryLevel = it.preciseLevel)
                }
            }
        }

        AnimatedVisibility(
            visible = showMetricGraph,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                batteryInfo?.let {
                    ChargingGraphCard(
                        currentNow = it.current,
                        power = it.power,
                        temp = it.temperature,
                        history = when (selectedMetric) {
                            BatteryMetric.CURRENT -> currentHistory
                            BatteryMetric.POWER -> powerHistory
                            BatteryMetric.TEMPERATURE -> tempHistory
                        },
                        chargerType = it.chargerType,
                        selectedMetric = selectedMetric,
                        onMetricSelected = { viewModel.selectMetric(it) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        batteryInfo?.let {
            CapacityGraphCard(
                remainingCapacity = it.remainingCapacity,
                estimatedMax = it.estimatedMaxCapacity,
                isCharging = it.isCharging
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
fun CapacityGraphCard(
    remainingCapacity: Int,
    estimatedMax: Int,
    isCharging: Boolean
) {
    val animatedRemaining by animateFloatAsState(
        targetValue = remainingCapacity.toFloat(),
        animationSpec = tween(1500),
        label = "rem"
    )
    
    val range = estimatedMax.toFloat().coerceAtLeast(1f)
    val t = if (isCharging) {
        animatedRemaining / range
    } else {
        (range - animatedRemaining) / range
    }

    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

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
                .graphicsLayer()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val yStart = if (isCharging) height else 0f
                    val yEnd = if (isCharging) 0f else height
                    
                    val path = Path().apply {
                        moveTo(0f, yStart)
                        cubicTo(width * 0.4f, yStart, width * 0.6f, yEnd, width, yEnd)
                    }
                    
                    drawPath(
                        path = path,
                        color = onSurfaceVariant.copy(alpha = 0.2f),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    val markerX = t * width
                    clipRect(right = markerX) {
                        drawPath(
                            path = path,
                            color = if (isCharging) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    
                    val it_val = 1 - t
                    val markerY = it_val * it_val * it_val * yStart + 3 * it_val * it_val * t * yStart + 3 * it_val * t * t * yEnd + t * t * t * yEnd
                    
                    drawCircle(
                        color = if (isCharging) Color(0xFF4CAF50) else Color(0xFFF44336),
                        radius = 12.dp.toPx(),
                        center = Offset(markerX, markerY),
                        alpha = 0.3f
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = Offset(markerX, markerY)
                    )
                    drawCircle(
                        color = if (isCharging) Color(0xFF4CAF50) else Color(0xFFF44336),
                        radius = 6.dp.toPx(),
                        center = Offset(markerX, markerY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                Text(
                    text = if (isCharging) "0 mAh" else "$estimatedMax mAh",
                    color = labelColor,
                    fontSize = 11.sp
                )
                Text(
                    text = if (isCharging) "$estimatedMax mAh" else "0 mAh",
                    color = labelColor,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ChargingGraphCard(
    currentNow: Int,
    power: Double,
    temp: Int,
    history: List<Number>,
    chargerType: String,
    selectedMetric: BatteryMetric,
    onMetricSelected: (BatteryMetric) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = when(selectedMetric) {
                        BatteryMetric.CURRENT -> "Current Now"
                        BatteryMetric.POWER -> "Power Output"
                        BatteryMetric.TEMPERATURE -> "Temperature"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = when(selectedMetric) {
                        BatteryMetric.CURRENT -> "${currentNow / 1000} mA"
                        BatteryMetric.POWER -> String.format("%.4f W", power)
                        BatteryMetric.TEMPERATURE -> "${temp / 10.0} °C"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = chargerType,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BatteryMetric.entries.forEach { metric ->
                val isSelected = metric == selectedMetric
                val bgColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, label = "chooser")
                val textColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, label = "chooserTxt")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(bgColor, shape = RoundedCornerShape(8.dp))
                        .clickable { onMetricSelected(metric) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(metric.name, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .graphicsLayer()
        ) {
            LineGraph(
                dataPoints = history,
                lineColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
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

    val floatData = remember(dataPoints) { dataPoints.map { it.toFloat() } }
    val maxVal = fixedMax ?: (floatData.maxOrNull() ?: 1f)
    val minVal = fixedMin ?: (floatData.minOrNull() ?: 0f)
    
    val animatedMax by animateFloatAsState(targetValue = maxVal, animationSpec = tween(500), label = "max")
    val animatedMin by animateFloatAsState(targetValue = minVal, animationSpec = tween(500), label = "min")
    
    val range = (animatedMax - animatedMin).coerceAtLeast(0.0001f)

    Canvas(modifier = modifier) {
        clipRect {
            if (floatData.size < 2) return@clipRect

            val widthPerPoint = size.width / (floatData.size - 1)
            val path = Path()
            val fillPath = Path()
            
            val getY = { valIndex: Int ->
                val value = floatData[valIndex]
                val normalized = ((value - animatedMin) / range).coerceIn(0f, 1f)
                size.height - normalized * size.height
            }

            val firstY = getY(0)
            path.moveTo(0f, firstY)
            fillPath.moveTo(0f, size.height)
            fillPath.lineTo(0f, firstY)

            for (i in 0 until floatData.size - 1) {
                val x1 = i * widthPerPoint
                val y1 = getY(i)
                val x2 = (i + 1) * widthPerPoint
                val y2 = getY(i + 1)

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
            val lastY = getY(floatData.size - 1)
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(180.dp)
            .graphicsLayer()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 15.dp.toPx()
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.1f), Color.Transparent),
                    center = center,
                    radius = radius + 20.dp.toPx()
                ),
                radius = radius + 20.dp.toPx(),
                center = center
            )

            val path = Path()
            val waveCount = 24
            val waveAmplitude = 8f
            
            for (angle in 0..360 step 1) {
                val theta = Math.toRadians(angle.toDouble())
                val r = radius + waveAmplitude * sin(waveCount * theta).toFloat()
                val x = center.x + r * cos(theta).toFloat()
                val y = center.y + r * sin(theta).toFloat()
                if (angle == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4.dp.toPx())
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
