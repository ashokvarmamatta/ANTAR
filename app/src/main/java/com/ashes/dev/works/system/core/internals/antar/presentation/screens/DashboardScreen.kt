package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Chip(text = it.deviceName)
                    Chip(text = "Android ${it.osVersion}")
                }
            }

            item {
                DigitalRamView(dashboard = it)
            }

            item {
                InternalStorageCard(dashboard = it)
            }

            item {
                BatteryCard(dashboard = it)
            }

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

            item {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        SmallInfoCard(
                            title = "APPLICATIONS",
                            value = "${it.appCount} Installed",
                            subtitle = "Updates Available",
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
private fun InternalStorageCard(dashboard: Dashboard) {
    val progress = dashboard.internalStoragePercentage.toFloat() / 100f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Internal Storage",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${dashboard.internalStoragePercentage}% Full",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Modern custom progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${dashboard.usedStorage} Used",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total ${dashboard.totalStorage}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BatteryCard(dashboard: Dashboard) {
    val isCharging = dashboard.batteryStatus == "Charging"
    val batteryLevel = dashboard.batteryLevel
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Power Source",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${batteryLevel.toInt()}% charged",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                BatteryIcon(
                    isCharging = isCharging, 
                    batteryLevel = batteryLevel,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "${dashboard.batteryStatus} • ${dashboard.batteryTemp} • ${dashboard.batteryVoltage}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
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

    val formattedUsed = String.format(Locale.US, "%.1f GB", usedRamDisplay)
    val formattedFree = String.format(Locale.US, "%.1f GB", freeRamDisplay)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                        text = "$formattedUsed Used",
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
                        text = "$formattedFree Free",
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
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = mainColor
            )
            Text(
                text = "%",
                style = MaterialTheme.typography.labelSmall,
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
    Surface(
        shape = RoundedCornerShape(16.dp), 
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = text, 
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SmallInfoCard(title: String, value: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon, 
                contentDescription = title, 
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BatteryIcon(
    isCharging: Boolean, 
    batteryLevel: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "battery")
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chargingPulse"
    )

    val currentAlpha = if (isCharging) pulseAlpha else 1.0f
    val color = if (isCharging) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
    
    Box(
        contentAlignment = Alignment.Center, 
        modifier = modifier
            .size(width = 48.dp, height = 64.dp) // Increased height and width
            .padding(2.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 3.dp.toPx()
            val cornerRadiusPx = 4.dp.toPx()
            val terminalHeightPx = 5.dp.toPx() // Slightly taller terminal
            
            val shellWidth = size.width
            val shellHeight = size.height - terminalHeightPx
            
            // Battery shell centered vertically
            drawRoundRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(0f, terminalHeightPx),
                size = androidx.compose.ui.geometry.Size(shellWidth, shellHeight),
                style = Stroke(width = strokeWidthPx),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            // Fill level
            val maxFillWidth = shellWidth - (strokeWidthPx * 2) - 6.dp.toPx()
            val maxFillHeight = shellHeight - (strokeWidthPx * 2) - 6.dp.toPx()
            
            val fillHeight = maxFillHeight * (batteryLevel / 100f)
            val fillTop = terminalHeightPx + shellHeight - fillHeight - strokeWidthPx - 3.dp.toPx()
            val fillLeft = strokeWidthPx + 3.dp.toPx()
            
            drawRect(
                color = color.copy(alpha = currentAlpha),
                topLeft = androidx.compose.ui.geometry.Offset(fillLeft, fillTop),
                size = androidx.compose.ui.geometry.Size(maxFillWidth, fillHeight)
            )

            // Battery terminal cap
            val terminalWidth = shellWidth * 0.4f
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset((shellWidth - terminalWidth) / 2, 0f),
                size = androidx.compose.ui.geometry.Size(terminalWidth, terminalHeightPx)
            )
        }
        
        if (isCharging) {
            Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = null,
                modifier = Modifier.size(28.dp), // Slightly larger icon
                tint = Color.White
            )
        }
    }
}

fun ClosedRange<Float>.random(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}
