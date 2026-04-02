package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.GradientEnd
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.GradientMid
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.GradientStart
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
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                color = AntarDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // App title with gradient text effect
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
                            color = AntarCyan,
                            letterSpacing = 2.sp
                        )
                    }

                    // Premium scrollable tab row
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        edgePadding = 12.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (pagerState.currentPage < tabPositions.size) {
                                val currentTab = tabPositions[pagerState.currentPage]
                                val targetPage = pagerState.targetPage
                                val targetTab = tabPositions.getOrNull(targetPage) ?: currentTab
                                val fraction = pagerState.currentPageOffsetFraction

                                val indicatorStart: androidx.compose.ui.unit.Dp
                                val indicatorEnd: androidx.compose.ui.unit.Dp

                                if (fraction >= 0) {
                                    indicatorStart = lerp(currentTab.left, targetTab.left, (fraction * 2f - 1f).coerceAtLeast(0f))
                                    indicatorEnd = lerp(currentTab.right, targetTab.right, (fraction * 2f).coerceAtMost(1f))
                                } else {
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
                                        .height(36.dp)
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    GradientStart.copy(alpha = 0.2f),
                                                    GradientMid.copy(alpha = 0.15f),
                                                    GradientEnd.copy(alpha = 0.1f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .zIndex(1f)
                                )
                            }
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        screens.forEachIndexed { index, screen ->
                            val isSelected = pagerState.currentPage == index
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) AntarCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                animationSpec = tween(300),
                                label = "tabColor"
                            )

                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .zIndex(2f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = textColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = screen.title,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Subtle gradient divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AntarCyan.copy(alpha = 0.3f),
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
            key(screens[page].route) {
                val isLocationScreen = screens[page] == Screen.Location
                val isCurrentPage = pagerState.currentPage == page

                if (isLocationScreen) {
                    if (isCurrentPage) {
                        LocationScreen()
                    } else {
                        Box(Modifier.fillMaxSize())
                    }
                } else {
                    when (screens[page]) {
                        Screen.Dashboard -> DashboardScreen()
                        Screen.Device -> DeviceScreen()
                        Screen.System -> SystemScreen()
                        Screen.Cpu -> CpuScreen()
                        Screen.Battery -> BatteryScreen()
                        Screen.Network -> NetworkScreen()
                        Screen.Storage -> StorageScreen()
                        Screen.Display -> DisplayScreen()
                        Screen.Sensors -> SensorsScreen()
                        Screen.Apps -> AppsScreen()
                        Screen.Camera -> CameraScreen()
                        Screen.Location -> { /* Handled above */ }
                    }
                }
            }
        }
    }
}

private fun lerp(start: androidx.compose.ui.unit.Dp, stop: androidx.compose.ui.unit.Dp, fraction: Float): androidx.compose.ui.unit.Dp {
    return start + (stop - start) * fraction
}
