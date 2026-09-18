package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.DashboardSummary
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components.BatteryCard
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components.PremiumChip
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components.QuickInfoCard
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components.RamCard
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components.StorageCard
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

private const val KHZ_PER_MHZ = 1000L
