package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.roundToInt
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.bounceClick
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
    val intensity = LocalAnimationIntensity.current

    // Scroll the pager to a given screen (used by Dashboard quick cards).
    val scrollToScreen: (Screen) -> Unit = { target ->
        val targetIndex = screens.indexOf(target)
        if (targetIndex >= 0) {
            coroutineScope.launch { pagerState.animateScrollToPage(targetIndex) }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.background
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
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { navController.navigate(Screen.Settings.route) }
                        ) {
                            Icon(
                                painter = painterResource(Screen.Settings.iconRes),
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Tab strip — a LazyRow of self-contained pills so we control spacing exactly.
                    // (ScrollableTabRow forces a minimum tab width that pushed the icons too far
                    // apart.) Each pill animates its OWN container colour + size together, so
                    // selection moves as one cohesive spring.
                    val tabListState = rememberLazyListState()
                    // Keep the active tab on screen, but only scroll when it's actually near an
                    // edge — otherwise neighbours (e.g. Dashboard) shouldn't be pushed off.
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
                    // Measured on-screen bounds of each tab (left & width in the strip's own
                    // coordinate space). This is the only reliable source for aligning the pill —
                    // it accounts for content padding, spacing and scroll automatically.
                    val tabLefts = remember { mutableStateMapOf<Int, Float>() }
                    val tabWidths = remember { mutableStateMapOf<Int, Float>() }
                    var stripCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .onGloballyPositioned { stripCoords = it }
                    ) {
                        // Pill anchored to the SELECTED tab's measured bounds; springs there on
                        // change so it's always perfectly centred on the active label.
                        val targetLeft = tabLefts[pagerState.currentPage]
                        val targetWidth = tabWidths[pagerState.currentPage]
                        val pillSpec: AnimationSpec<Float> =
                            if (intensity == AnimationIntensity.LOW) snap() else AntarMotion.spatial()
                        val animLeft by animateFloatAsState(targetLeft ?: 0f, pillSpec, label = "pillLeft")
                        val animWidth by animateFloatAsState(targetWidth ?: 0f, pillSpec, label = "pillWidth")
                        if ((targetWidth ?: 0f) > 0f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .offset { IntOffset(animLeft.roundToInt(), 0) }
                                    .height(36.dp)
                                    .width(with(density) { animWidth.toDp() })
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                    .border(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        RoundedCornerShape(18.dp)
                                    )
                            )
                        }

                        LazyRow(
                            state = tabListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemsIndexed(screens, key = { _, screen -> screen.route }) { index, screen ->
                                // Icon/label colour cross-fades in step with the sliding pill.
                                val pageOff = pagerState.currentPage + pagerState.currentPageOffsetFraction
                                val frac = if (intensity == AnimationIntensity.LOW) {
                                    if (pagerState.currentPage == index) 1f else 0f
                                } else (1f - kotlin.math.abs(pageOff - index)).coerceIn(0f, 1f)
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
                                                tabLefts[index] = strip.localPositionOf(coords, Offset.Zero).x
                                                tabWidths[index] = coords.size.width.toFloat()
                                            }
                                        }
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .bounceClick {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(screen.iconRes),
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(18.dp),
                                        tint = textColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = screen.title,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (frac > 0.5f) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
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
                        Screen.Dashboard -> DashboardScreen(onNavigate = scrollToScreen)
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
                        Screen.Settings -> { /* Not part of the pager — opened from the top-bar icon */ }
                    }
                }
            }
        }
    }
}
