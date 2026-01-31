package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random
import java.util.Locale

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
                    Chip(text = it.deviceName)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Chip(text = "Android ${it.osVersion}")
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // RAM Utilization
                DigitalRamView(dashboard = it)
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
fun DigitalRamView(dashboard: Dashboard) {
    val cardBackgroundColor = Color(0xFF90CAF9)
    val pathColor = Color(0xFF1565C0)

    var graphData by remember {
        mutableStateOf(List(15) { (dashboard.ramUsagePercentage.toFloat() - 5..dashboard.ramUsagePercentage.toFloat() + 5).random() })
    }

    var targetRamPercentage by remember { mutableStateOf(dashboard.ramUsagePercentage.toFloat()) }
    val animatedRamPercentage = remember { Animatable(dashboard.ramUsagePercentage.toFloat()) }
    
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(targetRamPercentage) {
        animatedRamPercentage.animateTo(
            targetValue = targetRamPercentage,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    DisposableEffect(dashboard) {
        val job = coroutineScope.launch {
            while (true) {
                delay(2000)
                val base = dashboard.ramUsagePercentage.toFloat()
                val variance = (base - 1.5f..base + 1.5f).random()
                targetRamPercentage = variance
                graphData = (graphData + variance).takeLast(15)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    val currentDisplayPercentage = animatedRamPercentage.value
    val totalRamVal = remember(dashboard.totalMemory) { 
        dashboard.totalMemory.replace(" GB", "").toFloatOrNull() ?: 1f 
    }
    val usedRamDisplay = (totalRamVal * currentDisplayPercentage) / 100
    val freeRamDisplay = totalRamVal - usedRamDisplay

    // Format to one decimal place as requested: X.X0
    val formattedUsed = String.format(Locale.US, "%.1f0", usedRamDisplay)
    val formattedFree = String.format(Locale.US, "%.1f0", freeRamDisplay)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth(0.65f)
                    .fillMaxHeight(0.7f)
            ) {
                SmoothedRamGraph(color = pathColor, data = graphData)
            }

            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RAM - ${dashboard.totalMemory} Total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$formattedUsed GB Used",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ScallopedProgressBar(
                        percentage = currentDisplayPercentage,
                        mainColor = Color(0xFF0D47A1)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$formattedFree GB Free",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScallopedProgressBar(percentage: Float, mainColor: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val progressStrokeWidth = 8.dp.toPx()

            val borderPath = Path().apply {
                val waves = 24
                val angleStep = 360f / waves
                val waveHeight = 1.1f

                for (i in 0..waves) {
                    val currentAngleRad = Math.toRadians((i * angleStep).toDouble())
                    val nextAngleRad = Math.toRadians(((i + 1) * angleStep).toDouble())
                    val midAngleRad = (currentAngleRad + nextAngleRad) / 2

                    val startX = center.x + radius * kotlin.math.cos(currentAngleRad).toFloat()
                    val startY = center.y + radius * kotlin.math.sin(currentAngleRad).toFloat()

                    val endX = center.x + radius * kotlin.math.cos(nextAngleRad).toFloat()
                    val endY = center.y + radius * kotlin.math.sin(nextAngleRad).toFloat()

                    val controlX = center.x + (radius * waveHeight) * kotlin.math.cos(midAngleRad).toFloat()
                    val controlY = center.y + (radius * waveHeight) * kotlin.math.sin(midAngleRad).toFloat()

                    if (i == 0) moveTo(startX, startY)
                    quadraticBezierTo(controlX, controlY, endX, endY)
                }
                close()
            }
            drawPath(path = borderPath, color = mainColor.copy(alpha = 0.2f))

            drawArc(
                color = mainColor.copy(alpha = 0.4f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = progressStrokeWidth)
            )
            drawArc(
                color = mainColor,
                startAngle = -90f,
                sweepAngle = 360 * (percentage / 100f),
                useCenter = false,
                style = Stroke(width = progressStrokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${percentage.toInt()}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = mainColor
            )
            Text(
                text = "%",
                style = MaterialTheme.typography.bodySmall,
                color = mainColor
            )
        }
    }
}

@Composable
fun SmoothedRamGraph(color: Color, data: List<Float>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height)

            var previousX = 0f
            var previousY = height - (data[0] / 100f * height)
            lineTo(previousX, previousY)

            for (i in 1 until data.size) {
                val currentX = (i.toFloat() / (data.size - 1)) * width
                val currentY = height - (data[i] / 100f * height)

                val controlX = (previousX + currentX) / 2f
                cubicTo(controlX, previousY, controlX, currentY, currentX, currentY)

                previousX = currentX
                previousY = currentY
            }

            lineTo(width, height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.1f))
            )
        )

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(text = text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
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

        drawRoundRect(
            color = color,
            size = size,
            style = Stroke(width = strokeWidth),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        val levelHeight = size.height * 0.6f
        val levelWidth = size.width - (strokeWidth * 2) - 4.dp.toPx()
        val levelTop = size.height - levelHeight - strokeWidth - 2.dp.toPx()
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(strokeWidth + 2.dp.toPx(), levelTop),
            size = androidx.compose.ui.geometry.Size(levelWidth, levelHeight)
        )

        val terminalWidth = size.width * 0.4f
        val terminalHeight = strokeWidth
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(size.width / 2 - terminalWidth / 2, 0f),
            size = androidx.compose.ui.geometry.Size(terminalWidth, terminalHeight)
        )
    }
}

fun ClosedRange<Float>.random(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}
