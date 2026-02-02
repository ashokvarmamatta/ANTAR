package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    val screens = listOf(
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

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // Compact App Name Title
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        edgePadding = 16.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (pagerState.currentPage < tabPositions.size) {
                                val currentTab = tabPositions[pagerState.currentPage]
                                val targetPage = pagerState.targetPage
                                val targetTab = tabPositions.getOrNull(targetPage) ?: currentTab
                                val fraction = pagerState.currentPageOffsetFraction

                                // Expressive horizontal leap logic for both directions
                                val indicatorStart: androidx.compose.ui.unit.Dp
                                val indicatorEnd: androidx.compose.ui.unit.Dp

                                if (fraction >= 0) {
                                    // Moving forward (Left to Right)
                                    // Right edge stretches first, then left edge catches up
                                    indicatorStart = lerp(currentTab.left, targetTab.left, (fraction * 2f - 1f).coerceAtLeast(0f))
                                    indicatorEnd = lerp(currentTab.right, targetTab.right, (fraction * 2f).coerceAtMost(1f))
                                } else {
                                    // Moving backward (Right to Left)
                                    // Left edge stretches first, then right edge catches up
                                    // fraction is negative (from 0 to -1)
                                    val absFraction = -fraction
                                    indicatorStart = lerp(currentTab.left, targetTab.left, (absFraction * 2f).coerceAtMost(1f))
                                    indicatorEnd = lerp(currentTab.right, targetTab.right, (absFraction * 2f - 1f).coerceAtLeast(0f))
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(Alignment.BottomStart)
                                        .offset(x = indicatorStart)
                                        .width(indicatorEnd - indicatorStart)
                                        .height(32.dp)
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .zIndex(1f)
                                )
                            }
                        },
                        modifier = Modifier.height(44.dp)
                    ) {
                        screens.forEachIndexed { index, screen ->
                            val isSelected = pagerState.currentPage == index
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                label = "textColor"
                            )

                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .zIndex(2f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = screen.title,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
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

private fun lerp(start: androidx.compose.ui.unit.Dp, stop: androidx.compose.ui.unit.Dp, fraction: Float): androidx.compose.ui.unit.Dp {
    return start + (stop - start) * fraction
}
