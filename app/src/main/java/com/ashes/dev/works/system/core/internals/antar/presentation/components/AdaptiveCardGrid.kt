package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/**
 * How many card columns fit the current window, keyed on the window size class (width first):
 * one column on phones, two from the medium breakpoint (600dp: unfolded foldables, small tablets,
 * split screen), three from the expanded breakpoint (840dp: tablets, desktop windows).
 */
@Composable
fun rememberCardColumns(): Int {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when {
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> 3
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 2
        else -> 1
    }
}

// motion:allow container only: every caller applies item motion (staggeredEntry / animateItem) to its own items

/**
 * The scrolling body of every info tab. Cards flow into [rememberCardColumns] columns, so a phone
 * shows one column and a tablet or unfolded device fills its width instead of stretching one
 * phone-sized column. Full-width items use `item(span = StaggeredGridItemSpan.FullLine)`.
 * Horizontal safe-drawing insets keep cards clear of a side navigation bar or cutout in landscape.
 */
@Composable
fun AdaptiveCardGrid(
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: LazyStaggeredGridScope.() -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(rememberCardColumns()),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = 14.dp,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}
