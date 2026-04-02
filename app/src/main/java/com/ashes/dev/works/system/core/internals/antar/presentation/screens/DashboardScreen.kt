package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val dashboard by viewModel.dashboardInfo.collectAsState()

    dashboard?.let {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Device name & OS badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    PremiumChip(text = it.deviceName)
                    PremiumChip(text = "Android ${it.osVersion}", accent = true)
                }
            }

            item { RamCard(dashboard = it) }
            item { StorageCard(dashboard = it) }
            item { BatteryCard(dashboard = it) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        QuickInfoCard(
                            title = "PROCESSOR",
                            value = it.processorName,
                            subtitle = it.processorDetails,
                            icon = Icons.Outlined.Memory,
                            accentColor = AntarPurple
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickInfoCard(
                            title = "SENSORS",
                            value = "${it.sensorCount} Available",
                            subtitle = "",
                            icon = Icons.Outlined.Sensors,
                            accentColor = AntarGreen
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        QuickInfoCard(
                            title = "APPLICATIONS",
                            value = "${it.appCount} Installed",
                            subtitle = "",
                            icon = Icons.Outlined.Apps,
                            accentColor = AntarBlue
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        QuickInfoCard(
                            title = "SYS HEALTH",
                            value = it.sysHealth,
                            subtitle = "Up: ${it.uptime}",
                            icon = Icons.Outlined.Verified,
                            accentColor = AntarCyan
                        )
                    }
                }
            }
        }
    }
}

// ── RAM Card ─────────────────────────────────────────────────────────

@Composable
private fun RamCard(dashboard: Dashboard) {
    val ramPct = dashboard.ramUsagePercentage.toFloat()
    val animatedPct by animateFloatAsState(
        targetValue = ramPct,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "ram"
    )

    val totalRam = remember(dashboard.totalMemory) {
        dashboard.totalMemory.replace(" GB", "").toFloatOrNull() ?: 1f
    }
    val usedRam = (totalRam * animatedPct) / 100
    val freeRam = totalRam - usedRam

    GradientHeaderCard {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular RAM gauge
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2

                    // Track
                    drawCircle(
                        color = AntarDimGray.copy(alpha = 0.3f),
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(AntarCyan, AntarBlue, AntarPurple)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * (animatedPct / 100f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedPct.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AntarCyan
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntarGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "RAM",
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${dashboard.totalMemory} Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                GradientProgressBar(
                    progress = animatedPct / 100f,
                    colors = listOf(AntarCyan, AntarBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", usedRam)} GB Used",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntarGray
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", freeRam)} GB Free",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AntarCyan
                    )
                }
            }
        }
    }
}

// ── Storage Card ─────────────────────────────────────────────────────

@Composable
private fun StorageCard(dashboard: Dashboard) {
    val progress = dashboard.internalStoragePercentage.toFloat() / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "storage"
    )

    PremiumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "INTERNAL STORAGE",
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarPurple,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "${dashboard.internalStoragePercentage}% Used",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Outlined.Storage,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = AntarPurple.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientProgressBar(
            progress = animatedProgress,
            height = 10.dp,
            colors = listOf(AntarPurple, AntarPink)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${dashboard.usedStorage} Used",
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
            Text(
                text = "Total ${dashboard.totalStorage}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Battery Card ─────────────────────────────────────────────────────

@Composable
private fun BatteryCard(dashboard: Dashboard) {
    val isCharging = dashboard.batteryStatus == "Charging"
    val batteryLevel = dashboard.batteryLevel
    val batteryColor = if (isCharging) AntarGreen else when {
        batteryLevel > 50 -> AntarCyan
        batteryLevel > 20 -> AntarOrange
        else -> AntarRed
    }

    val animatedLevel by animateFloatAsState(
        targetValue = batteryLevel,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "battery"
    )

    PremiumCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "POWER SOURCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = batteryColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "${animatedLevel.toInt()}% Charged",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${dashboard.batteryStatus} \u2022 ${dashboard.batteryTemp} \u2022 ${dashboard.batteryVoltage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }

            BatteryIcon(
                isCharging = isCharging,
                batteryLevel = batteryLevel,
                batteryColor = batteryColor,
                modifier = Modifier.size(width = 44.dp, height = 60.dp)
            )
        }
    }
}

// ── Quick Info Card ──────────────────────────────────────────────────

@Composable
private fun QuickInfoCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.06f))
            .border(0.5.dp, accentColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(26.dp),
                tint = accentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = AntarGray,
                letterSpacing = 1.sp
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
                    color = AntarGray,
                    maxLines = 1
                )
            }
        }
    }
}

// ── Premium Chip ─────────────────────────────────────────────────────

@Composable
private fun PremiumChip(text: String, accent: Boolean = false) {
    val chipColor = if (accent) AntarCyan.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = if (accent) AntarCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val textColor = if (accent) AntarCyan else MaterialTheme.colorScheme.onSurface

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = chipColor,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ── Battery Icon ─────────────────────────────────────────────────────

@Composable
fun BatteryIcon(
    isCharging: Boolean,
    batteryLevel: Float,
    modifier: Modifier = Modifier,
    batteryColor: Color = if (isCharging) AntarGreen else AntarCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "battery")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val currentAlpha = if (isCharging) pulseAlpha else 1.0f

    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(4.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 2.5.dp.toPx()
            val cornerRadiusPx = 4.dp.toPx()
            val terminalHeightPx = 4.dp.toPx()

            val shellWidth = size.width
            val shellHeight = size.height - terminalHeightPx

            drawRoundRect(
                color = batteryColor.copy(alpha = 0.6f),
                topLeft = Offset(0f, terminalHeightPx),
                size = Size(shellWidth, shellHeight),
                style = Stroke(width = strokeWidthPx),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            val maxFillWidth = shellWidth - (strokeWidthPx * 2) - 4.dp.toPx()
            val maxFillHeight = shellHeight - (strokeWidthPx * 2) - 4.dp.toPx()

            val fillHeight = maxFillHeight * (batteryLevel / 100f)
            val fillTop = terminalHeightPx + shellHeight - fillHeight - strokeWidthPx - 2.dp.toPx()
            val fillLeft = strokeWidthPx + 2.dp.toPx()

            drawRoundRect(
                color = batteryColor.copy(alpha = currentAlpha),
                topLeft = Offset(fillLeft, fillTop),
                size = Size(maxFillWidth, fillHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            val terminalWidth = shellWidth * 0.4f
            drawRoundRect(
                color = batteryColor.copy(alpha = 0.6f),
                topLeft = Offset((shellWidth - terminalWidth) / 2, 0f),
                size = Size(terminalWidth, terminalHeightPx),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }

        if (isCharging) {
            Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}

fun ClosedRange<Float>.random(): Float {
    return kotlin.random.Random.nextFloat() * (endInclusive - start) + start
}
