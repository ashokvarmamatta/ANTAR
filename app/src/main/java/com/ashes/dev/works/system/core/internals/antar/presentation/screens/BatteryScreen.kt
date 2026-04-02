package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryMetric
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.ChargingSession
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.HistoryRange
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = koinViewModel()) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val capacityHistory by viewModel.capacityHistory.collectAsState()
    val currentHistory by viewModel.currentHistory.collectAsState()
    val powerHistory by viewModel.powerHistory.collectAsState()
    val tempHistory by viewModel.tempHistory.collectAsState()
    val selectedMetric by viewModel.selectedMetric.collectAsState()
    val showMetricGraph by viewModel.showMetricGraph.collectAsState()

    val history24h by viewModel.history24h.collectAsState()
    val history7d by viewModel.history7d.collectAsState()
    val chargingSessions by viewModel.chargingSessions.collectAsState()
    val selectedRange by viewModel.selectedHistoryRange.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Battery Visualization
        batteryInfo?.let {
            Spacer(modifier = Modifier.height(24.dp))
            BatteryVisualization(
                level = it.preciseLevel,
                cycles = it.chargeCycles
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        batteryInfo?.let {
            CapacityHistoryCard(
                remainingCapacity = it.remainingCapacity,
                estimatedMax = it.estimatedMaxCapacity,
                isCharging = it.isCharging,
                history = capacityHistory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Level History Card (from DB)
        BatteryHistoryCard(
            history24h = history24h,
            history7d = history7d,
            selectedRange = selectedRange,
            onRangeSelected = { viewModel.setHistoryRange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Charging Sessions
        if (chargingSessions.isNotEmpty()) {
            ChargingSessionsCard(sessions = chargingSessions)
            Spacer(modifier = Modifier.height(16.dp))
        }

        MetricsSection(
            showMetricGraph = showMetricGraph,
            onToggleGraph = { viewModel.toggleMetricGraph() },
            selectedMetric = selectedMetric,
            onMetricSelected = { viewModel.setMetric(it) },
            currentHistory = currentHistory,
            powerHistory = powerHistory,
            tempHistory = tempHistory
        )

        Spacer(modifier = Modifier.height(16.dp))

        batteryInfo?.let {
            PremiumCard {
                    SectionTitle(title = "Battery Info")
                    InfoRow("Health", it.health)
                    InfoRow("Battery Health", it.batteryHealthStatus)
                    InfoRow("Temperature", "${it.temperature / 10.0}\u00B0C")
                    InfoRow("Charger Type", it.chargerType)
                    InfoRow("Technology", it.technology)
                    InfoRow("Voltage", "${it.voltage} V")
                    InfoRow("Design Capacity", "${it.designCapacity} mAh")
                    InfoRow("Estimated Max Capacity", "${it.estimatedMaxCapacity} mAh")
                    InfoRow("Remaining Capacity", "${it.remainingCapacity} mAh")
                    InfoRow("Charge Cycles", "${it.chargeCycles}")
                    InfoRow("Current", "${it.current / 1000} mA")
                    InfoRow("Power", String.format("%.2f W", it.power))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Battery Level History Card (24h / 7d) ────────────────────────────

@Composable
fun BatteryHistoryCard(
    history24h: List<BatteryLog>,
    history7d: List<BatteryLog>,
    selectedRange: HistoryRange,
    onRangeSelected: (HistoryRange) -> Unit
) {
    val logs = if (selectedRange == HistoryRange.HOURS_24) history24h else history7d

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Battery History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Data point count
                Text(
                    text = "${logs.size} points",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Range toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onClick = { onRangeSelected(HistoryRange.HOURS_24) },
                    selected = selectedRange == HistoryRange.HOURS_24
                ) {
                    Text("Last 24h", fontSize = 12.sp)
                }
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    onClick = { onRangeSelected(HistoryRange.DAYS_7) },
                    selected = selectedRange == HistoryRange.DAYS_7
                ) {
                    Text("Last 7 days", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (logs.size >= 2) {
                // Battery level graph
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    BatteryHistoryGraph(
                        logs = logs,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val timeFormat = if (selectedRange == HistoryRange.HOURS_24) {
                        SimpleDateFormat("HH:mm", Locale.getDefault())
                    } else {
                        SimpleDateFormat("MMM dd", Locale.getDefault())
                    }
                    Text(
                        text = timeFormat.format(Date(logs.first().timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = timeFormat.format(Date(logs.last().timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                val avgLevel = logs.map { it.batteryLevel }.average()
                val minLevel = logs.minOf { it.batteryLevel }
                val maxLevel = logs.maxOf { it.batteryLevel }
                val drainPerHour = if (logs.size >= 2) {
                    val firstNonCharging = logs.filter { !it.isCharging }
                    if (firstNonCharging.size >= 2) {
                        val timeDiffHours = (firstNonCharging.last().timestamp - firstNonCharging.first().timestamp) / 3_600_000.0
                        if (timeDiffHours > 0) {
                            (firstNonCharging.first().batteryLevel - firstNonCharging.last().batteryLevel) / timeDiffHours
                        } else null
                    } else null
                } else null

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HistoryStatItem("Avg", "${String.format("%.0f", avgLevel)}%")
                    HistoryStatItem("Min", "$minLevel%")
                    HistoryStatItem("Max", "$maxLevel%")
                    if (drainPerHour != null && drainPerHour > 0) {
                        HistoryStatItem("Drain/hr", "${String.format("%.1f", drainPerHour)}%")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "App just installed!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Need more data to show (at least 30 min)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Battery is tracked in the background automatically",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BatteryHistoryGraph(
    logs: List<BatteryLog>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val chargingColor = Color(0xFF4CAF50)
    val dischargingColor = Color(0xFFF44336)
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
        if (logs.size < 2) return@Canvas

        val leftPadding = 40.dp.toPx()
        val graphWidth = size.width - leftPadding
        val graphHeight = size.height

        // Draw horizontal grid lines and Y-axis labels
        for (pct in listOf(0, 25, 50, 75, 100)) {
            val y = graphHeight - (pct / 100f) * graphHeight
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw battery level line with color segments
        val widthPerPoint = graphWidth / (logs.size - 1)

        val path = Path()
        val fillPath = Path()

        val getY = { index: Int ->
            graphHeight - (logs[index].batteryLevel / 100f) * graphHeight
        }

        val firstY = getY(0)
        path.moveTo(leftPadding, firstY)
        fillPath.moveTo(leftPadding, graphHeight)
        fillPath.lineTo(leftPadding, firstY)

        for (i in 0 until logs.size - 1) {
            val x1 = leftPadding + i * widthPerPoint
            val y1 = getY(i)
            val x2 = leftPadding + (i + 1) * widthPerPoint
            val y2 = getY(i + 1)

            val cx = x1 + (x2 - x1) / 2f
            path.cubicTo(cx, y1, cx, y2, x2, y2)
            fillPath.cubicTo(cx, y1, cx, y2, x2, y2)

            // Draw colored segment based on charging state
            val segmentPath = Path().apply {
                moveTo(x1, y1)
                cubicTo(cx, y1, cx, y2, x2, y2)
            }
            drawPath(
                path = segmentPath,
                color = if (logs[i].isCharging) chargingColor else dischargingColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        fillPath.lineTo(size.width, graphHeight)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent),
                startY = 0f,
                endY = graphHeight
            )
        )

        // Draw end point indicator
        val lastX = leftPadding + (logs.size - 1) * widthPerPoint
        val lastY = getY(logs.size - 1)
        val endColor = if (logs.last().isCharging) chargingColor else dischargingColor
        drawCircle(color = endColor, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(color = endColor.copy(alpha = 0.3f), radius = 8.dp.toPx(), center = Offset(lastX, lastY))
    }
}

// ── Charging Sessions Card ───────────────────────────────────────────

@Composable
fun ChargingSessionsCard(sessions: List<ChargingSession>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Charging Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${sessions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    sessions.reversed().forEach { session ->
                        ChargingSessionItem(session)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChargingSessionItem(session: ChargingSession) {
    val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val durationMinutes = (session.endTime - session.startTime) / 60_000
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    val durationText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    val gained = session.endLevel - session.startLevel

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Charge indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = timeFormat.format(Date(session.startTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${session.startLevel}% \u2192 ${session.endLevel}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "+${gained}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

// ── Original components (unchanged) ──────────────────────────────────

@Composable
fun BatteryVisualization(level: Double, cycles: Int) {
    val animatedLevel by animateFloatAsState(targetValue = level.toFloat(), label = "level")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(220.dp)
    ) {
        // Percentage Text in the middle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.1f", animatedLevel),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                )
            }
        }

        // Circular Progress exactly fitting the Box
        CircularBatteryProgress(
            progress = animatedLevel / 100f,
            modifier = Modifier.fillMaxSize()
        )

        // Cycle count badge positioned on the bottom edge of the circle
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 0.dp)
                .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
        ) {
            Text(
                text = "$cycles Cycles",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun CircularBatteryProgress(progress: Float, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val strokeWidthPx = 14.dp.toPx()
        val drawSize = size.minDimension - strokeWidthPx

        // Track
        drawCircle(
            color = trackColor,
            radius = drawSize / 2,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        // Animated progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset((size.width - drawSize) / 2, (size.height - drawSize) / 2),
            size = Size(drawSize, drawSize),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun MetricsSection(
    showMetricGraph: Boolean,
    onToggleGraph: () -> Unit,
    selectedMetric: BatteryMetric,
    onMetricSelected: (BatteryMetric) -> Unit,
    currentHistory: List<Int>,
    powerHistory: List<Double>,
    tempHistory: List<Int>
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleGraph() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Battery Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (showMetricGraph) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = showMetricGraph,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        BatteryMetric.entries.forEachIndexed { index, metric ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = BatteryMetric.entries.size),
                                onClick = { onMetricSelected(metric) },
                                selected = selectedMetric == metric
                            ) {
                                Text(metric.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        val history: List<Number> = when (selectedMetric) {
                            BatteryMetric.CURRENT -> currentHistory
                            BatteryMetric.POWER -> powerHistory
                            BatteryMetric.TEMPERATURE -> tempHistory
                        }

                        if (history.isNotEmpty()) {
                            LineGraph(
                                dataPoints = history,
                                lineColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No data", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
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
