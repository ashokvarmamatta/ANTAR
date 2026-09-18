package com.ashes.dev.works.system.core.internals.antar.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.AppsScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.BatteryScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.CameraScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.cpu.CpuScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.DashboardScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.device.DeviceScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.display.DisplayScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.location.LocationScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.main.components.TabStrip
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import com.ashes.dev.works.system.core.internals.antar.presentation.network.NetworkScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.SensorsScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.StorageScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.system.SystemScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { pagerScreens.size })
    val coroutineScope = rememberCoroutineScope()

    // Scroll the pager to a given screen (used by Dashboard quick cards).
    val scrollToScreen: (Screen) -> Unit = { target ->
        val targetIndex = pagerScreens.indexOf(target)
        if (targetIndex >= 0) {
            coroutineScope.launch { pagerState.animateScrollToPage(targetIndex) }
        }
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                // Top + horizontal safe-drawing insets: clear of the status bar, a side
                // navigation bar and display cutouts in landscape.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp,
                            modifier = Modifier.weight(1f)
                        )
                        val settingsSource = remember { MutableInteractionSource() }
                        IconButton(
                            onClick = { navController.navigate(Screen.Settings.route) },
                            interactionSource = settingsSource,
                            modifier = Modifier.pressScale(settingsSource)
                        ) {
                            Icon(
                                painter = painterResource(Screen.Settings.iconRes),
                                contentDescription = stringResource(R.string.main_open_settings),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    TabStrip(pagerState = pagerState, onSelect = { index ->
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    })

                    // Subtle gradient divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 1,
            pageSpacing = 0.dp
        ) { page ->
            key(pagerScreens[page].route) {
                // Location (GPS) and Apps (installed-package scan) only compose on their own page,
                // never as the pre-composed neighbour — both disclosures promise "only when you open it".
                val isOnDemandScreen = pagerScreens[page] == Screen.Location || pagerScreens[page] == Screen.Apps
                val isCurrentPage = pagerState.currentPage == page

                if (isOnDemandScreen) {
                    if (!isCurrentPage) {
                        Box(Modifier.fillMaxSize())
                    } else if (pagerScreens[page] == Screen.Location) {
                        LocationScreen()
                    } else {
                        AppsScreen()
                    }
                } else {
                    when (pagerScreens[page]) {
                        Screen.Dashboard -> DashboardScreen(onNavigate = scrollToScreen)
                        Screen.Device -> DeviceScreen()
                        Screen.System -> SystemScreen()
                        Screen.Cpu -> CpuScreen()
                        Screen.Battery -> BatteryScreen()
                        Screen.Network -> NetworkScreen()
                        Screen.Storage -> StorageScreen()
                        Screen.Display -> DisplayScreen()
                        Screen.Sensors -> SensorsScreen()
                        Screen.Camera -> CameraScreen()
                        Screen.Apps, Screen.Location -> { /* Handled above */ }
                        Screen.Settings -> { /* Not part of the pager — opened from the top-bar icon */ }
                    }
                }
            }
        }
    }
}

internal val pagerScreens = listOf(
    Screen.Dashboard,
    Screen.Device,
    Screen.System,
    Screen.Cpu,
    Screen.Battery,
    Screen.Location,
    Screen.Network,
    Screen.Storage,
    Screen.Display,
    Screen.Sensors,
    Screen.Apps,
    Screen.Camera
)
