package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.BatteryHistoryCard
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.BatteryInfoCard
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.BatteryVisualization
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.CapacityHistoryCard
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.ChargingSessionItem
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.ChargingSessionsHeader
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.components.MetricsCard
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import org.koin.androidx.compose.koinViewModel

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "batteryState"
    ) { state ->
        when (state) {
            BatteryUiState.Loading -> LoadingSkeleton()
            is BatteryUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::retry)
            is BatteryUiState.Content -> BatteryContent(
                state = state,
                onRangeSelected = viewModel::selectHistoryRange,
                onToggleMetricGraph = viewModel::toggleMetricGraph,
                onMetricSelected = viewModel::selectMetric
            )
        }
    }
}

@Composable
private fun BatteryContent(
    state: BatteryUiState.Content,
    onRangeSelected: (HistoryRange) -> Unit,
    onToggleMetricGraph: () -> Unit,
    onMetricSelected: (BatteryMetric) -> Unit
) {
    var sessionsExpanded by rememberSaveable { mutableStateOf(false) }
    val sessionTimeFormatter = rememberTimeFormatter(TimeStyle.DAY_TIME)
    val battery = state.battery

    AdaptiveCardGrid {
        item(key = "level", span = StaggeredGridItemSpan.FullLine) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .staggeredEntry(0),
                contentAlignment = Alignment.Center
            ) {
                BatteryVisualization(level = battery.preciseLevelPercent, cycles = battery.chargeCycles)
            }
        }

        item(key = "capacity") {
            CapacityHistoryCard(
                remainingCapacityMah = battery.remainingCapacityMah,
                isCharging = battery.isCharging,
                history = state.live.remainingCapacityMah,
                modifier = Modifier.staggeredEntry(1)
            )
        }

        item(key = "history") {
            BatteryHistoryCard(
                history = state.history,
                onRangeSelected = onRangeSelected,
                modifier = Modifier.staggeredEntry(2)
            )
        }

        if (state.chargingSessions.isNotEmpty()) {
            item(key = "sessions", span = StaggeredGridItemSpan.FullLine) {
                ChargingSessionsHeader(
                    sessionCount = state.chargingSessions.size,
                    expanded = sessionsExpanded,
                    onToggle = { sessionsExpanded = !sessionsExpanded },
                    modifier = Modifier.animateItem().staggeredEntry(3)
                )
            }
            if (sessionsExpanded) {
                items(
                    items = state.chargingSessions,
                    key = { session -> "session_${session.startTimeMillis}" },
                    span = { StaggeredGridItemSpan.FullLine }
                ) { session ->
                    ChargingSessionItem(
                        session = session,
                        formatter = sessionTimeFormatter,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        item(key = "metrics") {
            MetricsCard(
                isGraphVisible = state.isMetricGraphVisible,
                onToggleGraph = onToggleMetricGraph,
                selectedMetric = state.selectedMetric,
                onMetricSelected = onMetricSelected,
                live = state.live,
                modifier = Modifier.animateItem().staggeredEntry(4)
            )
        }

        item(key = "info") {
            BatteryInfoCard(battery = battery, modifier = Modifier.animateItem().staggeredEntry(5))
        }
    }
}
