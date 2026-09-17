package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.spatialSpec
import kotlin.math.roundToInt

private val CardShape = RoundedCornerShape(24.dp)

@Composable
private fun BatteryCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        content()
    }
}

// ── Level ring ───────────────────────────────────────────────────────

@Composable
internal fun BatteryVisualization(level: Double, cycles: Int?, modifier: Modifier = Modifier) {
    val animatedLevel = animateFloatAsState(
        targetValue = level.toFloat(),
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "level"
    )
    val description = stringResource(R.string.battery_cd_level_ring, level.roundToInt())

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(220.dp)
            .clearAndSetSemantics { contentDescription = description }
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            LevelNumber(level = { animatedLevel.value })
            Text(
                text = stringResource(R.string.battery_unit_percent),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp, start = 2.dp),
                maxLines = 1,
                softWrap = false
            )
        }

        CircularBatteryProgress(
            progress = { animatedLevel.value / 100f },
            modifier = Modifier.fillMaxSize()
        )

        if (cycles != null) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
            ) {
                Text(
                    text = pluralResource(R.plurals.battery_cycles_count, cycles),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/** Only this text recomposes per animation frame; the ring itself redraws in the draw phase. */
@Composable
private fun LevelNumber(level: () -> Float) {
    Text(
        text = stringResource(R.string.battery_value_level_number, level()),
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        softWrap = false
    )
}

// ── Live capacity ────────────────────────────────────────────────────

@Composable
internal fun CapacityHistoryCard(
    remainingCapacityMah: Int?,
    isCharging: Boolean,
    history: List<Int>,
    modifier: Modifier = Modifier
) {
    val stateColor = if (isCharging) ChargingGreen else DischargingRed
    val bounds = remember(history) {
        if (history.isEmpty()) {
            null
        } else {
            val min = history.min().toFloat()
            val max = history.max().toFloat()
            val padding = (max - min).coerceAtLeast(10f) * 0.1f
            (min - padding) to (max + padding)
        }
    }

    BatteryCard(modifier = modifier.height(240.dp)) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = stringResource(if (isCharging) R.string.battery_state_charging else R.string.battery_state_discharging),
                        color = stateColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = remainingCapacityMah?.let { stringResource(R.string.battery_value_mah, it) } ?: NO_VALUE,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = stringResource(R.string.battery_cd_battery),
                    tint = if (isCharging) ChargingGreen else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (bounds != null) {
                    LineGraph(
                        dataPoints = history,
                        lineColor = stateColor,
                        modifier = Modifier.fillMaxSize(),
                        fixedMin = bounds.first,
                        fixedMax = bounds.second
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.battery_collecting_data),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (history.isNotEmpty()) {
                    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    Text(text = stringResource(R.string.battery_value_mah, history.first()), color = labelColor, fontSize = 11.sp)
                    Text(text = stringResource(R.string.battery_value_mah, history.last()), color = labelColor, fontSize = 11.sp)
                }
            }
        }
    }
}

// ── Stored history (24 h / 7 d) ──────────────────────────────────────

@Composable
internal fun BatteryHistoryCard(
    history: BatteryHistory,
    onRangeSelected: (HistoryRange) -> Unit,
    modifier: Modifier = Modifier
) {
    BatteryCard(modifier = modifier) {
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
                        text = stringResource(R.string.battery_history_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = pluralResource(R.plurals.battery_history_points, history.points.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                HistoryRange.entries.forEachIndexed { index, range ->
                    val interactionSource = remember { MutableInteractionSource() }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = HistoryRange.entries.size),
                        onClick = { onRangeSelected(range) },
                        selected = history.range == range,
                        interactionSource = interactionSource,
                        modifier = Modifier.pressScale(interactionSource)
                    ) {
                        Text(text = stringResource(range.labelRes()), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.hasChart) {
                HistoryChartWithStats(history)
            } else {
                HistoryEmpty()
            }
        }
    }
}

@Composable
private fun HistoryChartWithStats(history: BatteryHistory) {
    val formatter = rememberTimeFormatter(
        if (history.range == HistoryRange.HOURS_24) TimeStyle.TIME else TimeStyle.DAY
    )

    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        BatteryHistoryGraph(points = history.points, modifier = Modifier.fillMaxSize())
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = formatter.format(history.points.first().timestampMillis),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatter.format(history.points.last().timestampMillis),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        history.averageLevelPercent?.let {
            HistoryStatItem(R.string.battery_stat_average, stringResource(R.string.battery_value_percent_rounded, it))
        }
        history.minLevelPercent?.let {
            HistoryStatItem(R.string.battery_stat_min, stringResource(R.string.battery_value_percent, it))
        }
        history.maxLevelPercent?.let {
            HistoryStatItem(R.string.battery_stat_max, stringResource(R.string.battery_value_percent, it))
        }
        history.drainPercentPerHour?.let {
            HistoryStatItem(R.string.battery_stat_drain_per_hour, stringResource(R.string.battery_value_percent_decimal, it))
        }
    }
}

@Composable
private fun HistoryEmpty() {
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
                text = stringResource(R.string.battery_history_empty_title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.battery_history_empty_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.battery_history_empty_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HistoryStatItem(@StringRes label: Int, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Charging sessions ────────────────────────────────────────────────

@Composable
internal fun ChargingSessionsHeader(
    sessionCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    BatteryCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick(onClick = onToggle)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = ChargingGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.battery_sessions_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pluralResource(R.plurals.battery_sessions_count, sessionCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                ExpandIcon(expanded)
            }
        }
    }
}

@Composable
private fun ExpandIcon(expanded: Boolean) {
    Icon(
        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        contentDescription = stringResource(if (expanded) R.string.battery_cd_collapse else R.string.battery_cd_expand)
    )
}

@Composable
internal fun ChargingSessionItem(
    session: ChargingSession,
    formatter: TimeFormatter,
    modifier: Modifier = Modifier
) {
    val hours = session.durationMinutes / 60
    val minutes = session.durationMinutes % 60
    val durationText = if (hours > 0) {
        stringResource(R.string.battery_duration_hours_minutes, hours, minutes)
    } else {
        stringResource(R.string.battery_duration_minutes, minutes)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(ChargingGreen, ChargingGreenLight)),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatter.format(session.startTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.battery_session_levels, session.startLevelPercent, session.endLevelPercent),
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
                    text = stringResource(R.string.battery_session_gained, session.gainedPercent),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChargingGreen
                )
            }
        }
    }
}

// ── Live metrics ─────────────────────────────────────────────────────

@Composable
internal fun MetricsCard(
    isGraphVisible: Boolean,
    onToggleGraph: () -> Unit,
    selectedMetric: BatteryMetric,
    onMetricSelected: (BatteryMetric) -> Unit,
    live: LiveSamples,
    modifier: Modifier = Modifier
) {
    val intensity = LocalAnimationIntensity.current

    BatteryCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(onClick = onToggleGraph),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.battery_metrics_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                ExpandIcon(isGraphVisible)
            }

            AnimatedVisibility(
                visible = isGraphVisible,
                enter = expandVertically(intensity.spatialSpec()) + fadeIn(intensity.effectsSpec()),
                exit = shrinkVertically(intensity.spatialSpec()) + fadeOut(intensity.effectsSpec())
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        BatteryMetric.entries.forEachIndexed { index, metric ->
                            val interactionSource = remember { MutableInteractionSource() }
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = BatteryMetric.entries.size),
                                onClick = { onMetricSelected(metric) },
                                selected = selectedMetric == metric,
                                interactionSource = interactionSource,
                                modifier = Modifier.pressScale(interactionSource)
                            ) {
                                Text(text = stringResource(metric.labelRes()), fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val samples: List<Number> = when (selectedMetric) {
                        BatteryMetric.CURRENT -> live.currentMicroAmps
                        BatteryMetric.POWER -> live.powerWatts
                        BatteryMetric.TEMPERATURE -> live.temperatureDeciCelsius
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        if (samples.isNotEmpty()) {
                            LineGraph(
                                dataPoints = samples,
                                lineColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(R.string.battery_no_data),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
