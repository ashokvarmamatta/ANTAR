package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(navController: NavController) {
    val screens = listOf(
        Screen.Dashboard,
        Screen.Device,
        Screen.System,
        Screen.Cpu,
        Screen.Location,
        Screen.Network,
        Screen.Storage,
        Screen.Battery,
        Screen.Display,
        Screen.Sensors,
        Screen.Apps,
        Screen.Camera
    )
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold {
        innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                screens.forEachIndexed { index, screen ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { 
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = screen.title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (screens[page]) {
                    Screen.Dashboard -> DashboardScreen()
                    Screen.Device -> DeviceScreen()
                    Screen.System -> SystemScreen()
                    Screen.Cpu -> CpuScreen()
                    Screen.Location -> LocationScreen()
                    Screen.Network -> NetworkScreen()
                    Screen.Storage -> StorageScreen()
                    Screen.Battery -> BatteryScreen()
                    Screen.Display -> DisplayScreen()
                    Screen.Sensors -> SensorsScreen()
                    Screen.Apps -> AppsScreen()
                    Screen.Camera -> CameraScreen()
                }
            }
        }
    }
}
