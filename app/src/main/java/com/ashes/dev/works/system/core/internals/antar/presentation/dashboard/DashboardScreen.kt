package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientProgressBar
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDimGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPink
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.ambientFloat
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.core.ui.formatBytes
import com.ashes.dev.works.system.core.internals.antar.core.ui.formatPercent
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.DashboardSummary
import com.ashes.dev.works.system.core.internals.antar.domain.model.MemoryUsage
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    // Activity-scoped: the same instance MainActivity already uses for the splash gate, so the
    // dashboard pipeline (battery receiver, poll, storage reads) runs once, not twice.
    viewModel: DashboardViewModel = koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity),
    onNavigate: (Screen) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "dashboardState"
    ) { state ->
        when (state) {
            DashboardUiState.Loading -> LoadingSkeleton()
            is DashboardUiState.Content -> DashboardContent(state.summary, onNavigate)
        }
    }
}

@Composable
private fun DashboardContent(summary: DashboardSummary, onNavigate: (Screen) -> Unit) {
    AdaptiveCardGrid {
        item(key = "chips", span = StaggeredGridItemSpan.FullLine) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntry(0),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                summary.deviceName?.let { PremiumChip(text = it) }
                summary.androidVersion?.let {
                    PremiumChip(text = stringResource(R.string.dashboard_android_version, it), accent = true)
                }
            }
        }

        summary.ram?.let { ram ->
            item(key = "ram") { RamCard(ram = ram, modifier = Modifier.staggeredEntry(1)) }
        }
        summary.internalStorage?.let { storage ->
            item(key = "storage") {
                StorageCard(
                    storage = storage,
                    modifier = Modifier
                        .staggeredEntry(2)
                        .bounceClick { onNavigate(Screen.Storage) }
                )
            }
        }
        summary.battery?.let { battery ->
            item(key = "battery") {
                BatteryCard(
                    battery = battery,
                    modifier = Modifier
                        .staggeredEntry(3)
                        .bounceClick { onNavigate(Screen.Battery) }
                )
            }
        }

        item(key = "quick", span = StaggeredGridItemSpan.FullLine) {
            Column(
                modifier = Modifier.staggeredEntry(4),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickInfoCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .bounceClick { onNavigate(Screen.Cpu) },
                        title = R.string.dashboard_processor,
                        value = summary.socName ?: NO_VALUE,
                        subtitle = summary.coreCount?.let { cores ->
                            val coreText = pluralStringResource(R.plurals.cpu_core_count, cores, cores)
                            summary.cpuFrequencyKhz?.let {
                                stringResource(R.string.dashboard_cores_and_frequency, coreText, it / KHZ_PER_MHZ)
                            } ?: coreText
                        },
                        icon = Icons.Outlined.Memory,
                        accentColor = AntarPurple
                    )
                    QuickInfoCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .bounceClick { onNavigate(Screen.Sensors) },
                        title = R.string.dashboard_sensors,
                        value = summary.sensorCount?.let { pluralStringResource(R.plurals.sensors_count, it, it) } ?: NO_VALUE,
                        subtitle = null,
                        icon = Icons.Outlined.Sensors,
                        accentColor = AntarGreen
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickInfoCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .bounceClick { onNavigate(Screen.Apps) },
                        title = R.string.dashboard_applications,
                        value = summary.appCount?.let { pluralStringResource(R.plurals.apps_count, it, it) } ?: NO_VALUE,
                        subtitle = if (summary.appCount == null) stringResource(R.string.dashboard_apps_tap_to_allow) else null,
                        icon = Icons.Outlined.Apps,
                        accentColor = AntarBlue
                    )
                    QuickInfoCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .bounceClick { onNavigate(Screen.System) },
                        title = R.string.dashboard_uptime,
                        value = summary.uptimeMillis?.let { formatUptime(it) } ?: NO_VALUE,
                        subtitle = summary.androidVersion?.let { stringResource(R.string.dashboard_android_version, it) },
                        icon = Icons.Outlined.Schedule,
                        accentColor = AntarCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun formatUptime(millis: Long): String {
    val totalSeconds = millis / 1000
    return stringResource(
        R.string.system_value_uptime,
        totalSeconds / 86_400,
        (totalSeconds % 86_400) / 3_600,
        (totalSeconds % 3_600) / 60,
        totalSeconds % 60
    )
}

// ── RAM Card ─────────────────────────────────────────────────────────

@Composable
private fun RamCard(ram: MemoryUsage, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = ram.usedFraction,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "ram"
    )
    val trackColor = AntarDimGray.copy(alpha = 0.3f)
    // Theme-following colours are read in composition, not inside the Canvas draw lambda.
    val arcColors = listOf(AntarCyan, AntarBlue, AntarPurple)

    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    drawCircle(color = trackColor, radius = radius, style = Stroke(width = strokeWidth))
                    drawArc(
                        brush = Brush.sweepGradient(colors = arcColors),
                        startAngle = -90f,
                        sweepAngle = 360f * fraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth)
                    )
                }
                Text(
                    text = formatPercent(fraction),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AntarCyan
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_ram),
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_total, formatBytes(ram.totalBytes)),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientProgressBar(progress = fraction, colors = listOf(AntarCyan, AntarBlue))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_used, formatBytes(ram.usedBytes)),
                        style = MaterialTheme.typography.labelSmall,
                        color = AntarGray
                    )
                    Text(
                        text = stringResource(R.string.dashboard_free, formatBytes(ram.availableBytes)),
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
private fun StorageCard(storage: VolumeUsage, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = storage.usedFraction,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "storage"
    )

    PremiumCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_internal_storage),
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarPurple,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_percent_used, formatPercent(storage.usedFraction)),
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
        GradientProgressBar(progress = fraction, height = 10.dp, colors = listOf(AntarPurple, AntarPink))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.dashboard_used, formatBytes(storage.usedBytes)),
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
            Text(
                text = stringResource(R.string.dashboard_total, formatBytes(storage.totalBytes)),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Battery Card ─────────────────────────────────────────────────────

@Composable
private fun BatteryCard(battery: Battery, modifier: Modifier = Modifier) {
    val level = battery.preciseLevelPercent.toFloat()
    val batteryColor = if (battery.isCharging) AntarGreen else when {
        level > 50 -> AntarCyan
        level > 20 -> AntarOrange
        else -> AntarRed
    }
    val animatedLevel by animateFloatAsState(
        targetValue = level,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "battery"
    )
    val details = listOfNotNull(
        stringResource(if (battery.isCharging) R.string.dashboard_charging else R.string.dashboard_discharging),
        battery.temperatureDeciCelsius?.let { stringResource(R.string.dashboard_temperature, it / 10f) },
        battery.voltageVolts?.let { stringResource(R.string.dashboard_voltage, it) }
    )

    PremiumCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_power_source),
                    style = MaterialTheme.typography.labelMedium,
                    color = batteryColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_percent_charged, animatedLevel.toInt()),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = details.joinToString(stringResource(R.string.dashboard_separator)),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }

            BatteryIcon(
                isCharging = battery.isCharging,
                batteryLevel = level,
                batteryColor = batteryColor,
                modifier = Modifier.size(width = 44.dp, height = 60.dp)
            )
        }
    }
}

// ── Quick Info Card ──────────────────────────────────────────────────

@Composable
private fun QuickInfoCard(
    @StringRes title: Int,
    value: String,
    subtitle: String?,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(accentColor.copy(alpha = 0.06f))
            .border(0.5.dp, accentColor.copy(alpha = 0.15f), shape)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = accentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(title),
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle.orEmpty(),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = AntarGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Chip ─────────────────────────────────────────────────────────────

@Composable
private fun PremiumChip(text: String, accent: Boolean = false) {
    val chipColor = if (accent) AntarCyan.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = if (accent) AntarCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val textColor = if (accent) AntarCyan else MaterialTheme.colorScheme.onSurface

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = chipColor,
        border = BorderStroke(0.5.dp, borderColor)
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
private fun BatteryIcon(
    isCharging: Boolean,
    batteryLevel: Float,
    modifier: Modifier = Modifier,
    batteryColor: Color = if (isCharging) AntarGreen else AntarCyan
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(4.dp)) {
        // The pulse exists only while charging, and its value is read inside the Canvas draw
        // lambda, so it never recomposes the card.
        val pulse = if (isCharging && LocalAnimationIntensity.current != AnimationIntensity.LOW) {
            rememberInfiniteTransition(label = "battery").ambientFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(AntarMotion.AMBIENT_QUICK_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "pulse"
            )
        } else {
            null
        }

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
                color = batteryColor.copy(alpha = pulse?.value ?: 1f),
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
                imageVector = Icons.Filled.BatteryChargingFull,
                contentDescription = stringResource(R.string.dashboard_charging),
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}

private const val KHZ_PER_MHZ = 1000L
