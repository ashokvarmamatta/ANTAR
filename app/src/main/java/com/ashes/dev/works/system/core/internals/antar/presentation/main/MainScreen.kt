package com.ashes.dev.works.system.core.internals.antar.presentation.main

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.AppsScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.BatteryScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.CameraScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.cpu.CpuScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.DashboardScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.device.DeviceScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.display.DisplayScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.location.LocationScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import com.ashes.dev.works.system.core.internals.antar.presentation.network.NetworkScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.SensorsScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.StorageScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.system.SystemScreen
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private val pagerScreens = listOf(
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

/**
 * The scrolling tab pills with a highlight that slides to the selected tab. Pager progress is read
 * through [derivedStateOf] per tab, rounded to 10 steps, so a swipe recomposes only the two tabs
 * the highlight is moving between instead of all twelve on every frame.
 */
@Composable
private fun TabStrip(pagerState: PagerState, onSelect: (Int) -> Unit) {
    val intensity = LocalAnimationIntensity.current
    val tabListState = rememberLazyListState()
    // Keep the active tab on screen, but only scroll when it's actually near an edge.
    LaunchedEffect(pagerState.currentPage) {
        val target = pagerState.currentPage
        val info = tabListState.layoutInfo
        val item = info.visibleItemsInfo.firstOrNull { it.index == target }
        if (item == null) {
            tabListState.animateScrollToItem(target)
        } else {
            val margin = 72
            val leftOverflow = (item.offset - info.viewportStartOffset) - margin
            val rightOverflow = (item.offset + item.size) - (info.viewportEndOffset - margin)
            if (leftOverflow < 0) {
                tabListState.animateScrollBy(leftOverflow.toFloat())
            } else if (rightOverflow > 0) {
                tabListState.animateScrollBy(rightOverflow.toFloat())
            }
        }
    }
    val density = LocalDensity.current
    // Measured bounds of each tab in the strip's coordinate space.
    val tabLefts = remember { mutableStateMapOf<Int, Float>() }
    val tabWidths = remember { mutableStateMapOf<Int, Float>() }
    var stripCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .onGloballyPositioned { stripCoords = it }
    ) {
        val targetLeft = tabLefts[pagerState.currentPage]
        val targetWidth = tabWidths[pagerState.currentPage]
        val pillSpec: AnimationSpec<Float> =
            if (intensity == AnimationIntensity.LOW) snap() else AntarMotion.spatial()
        val animLeft by animateFloatAsState(targetLeft ?: 0f, pillSpec, label = "pillLeft")
        val animWidth by animateFloatAsState(targetWidth ?: 0f, pillSpec, label = "pillWidth")
        if ((targetWidth ?: 0f) > 0f) {
            val pillShape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(animLeft.roundToInt(), 0) }
                    .height(36.dp)
                    .width(with(density) { animWidth.toDp() })
                    .clip(pillShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), pillShape)
            )
        }

        LazyRow(
            state = tabListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(pagerScreens, key = { _, screen -> screen.route }) { index, screen ->
                val frac by remember(index, intensity) {
                    derivedStateOf {
                        if (intensity == AnimationIntensity.LOW) {
                            if (pagerState.currentPage == index) 1f else 0f
                        } else {
                            val pageOff = pagerState.currentPage + pagerState.currentPageOffsetFraction
                            ((1f - abs(pageOff - index)).coerceIn(0f, 1f) * 10f).roundToInt() / 10f
                        }
                    }
                }
                val textColor = lerp(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    MaterialTheme.colorScheme.primary,
                    frac
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .onGloballyPositioned { coords ->
                            stripCoords?.let { strip ->
                                val left = strip.localPositionOf(coords, Offset.Zero).x
                                val width = coords.size.width.toFloat()
                                // Only write when the value changed, so layout passes don't loop.
                                if (tabLefts[index] != left) tabLefts[index] = left
                                if (tabWidths[index] != width) tabWidths[index] = width
                            }
                        }
                        .staggeredEntry(index)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .bounceClick { onSelect(index) }
                        .padding(horizontal = 12.dp)
                ) {
                    val title = stringResource(screen.title)
                    Icon(
                        painter = painterResource(screen.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (frac > 0.5f) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
