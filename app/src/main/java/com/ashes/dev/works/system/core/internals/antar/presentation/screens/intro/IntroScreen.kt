package com.ashes.dev.works.system.core.internals.antar.presentation.screens.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private data class IntroPage(
    val title: String,
    val description: String,
    val illustration: @Composable (Modifier) -> Unit
)

private val introPages = listOf(
    IntroPage(
        title = "Know your device",
        description = "Every spec on one live dashboard — CPU, battery, display, storage, network and more.",
        illustration = { DeviceIllustration(it) }
    ),
    IntroPage(
        title = "Straight from the silicon",
        description = "ANTAR reads the hardware directly: cores, clocks, thermals and chipset details.",
        illustration = { ChipIllustration(it) }
    ),
    IntroPage(
        title = "Sensors, live",
        description = "Watch every sensor stream in real time — motion, light, pressure and position.",
        illustration = { SensorsIllustration(it) }
    ),
    IntroPage(
        title = "Private by design",
        description = "Everything is read on your device and stays on your device. No accounts, no uploads.",
        illustration = { PrivacyIllustration(it) }
    )
)

@Composable
fun IntroScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == introPages.lastIndex
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .graphicsLayer {
                            // parallax drift + fade while swiping
                            translationX = pageOffset * size.width * 0.25f
                            alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f) * 0.6f
                        }
                ) {
                    introPages[page].illustration(Modifier.fillMaxSize())
                }

                Spacer(Modifier.height(36.dp))

                Text(
                    text = introPages[page].title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = cs.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = introPages[page].description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // bottom controls: skip · indicators · next / get started
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val skipAlpha by animateFloatAsState(
                if (isLastPage) 0f else 1f, tween(250), label = "skip"
            )
            TextButton(
                onClick = onFinish,
                enabled = !isLastPage,
                modifier = Modifier.alpha(skipAlpha)
            ) {
                Text("Skip", color = cs.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(introPages.size) { index ->
                    val selected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        if (selected) 26.dp else 8.dp, tween(300), label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (selected) cs.primary else cs.outlineVariant)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            val buttonWidth by animateDpAsState(
                if (isLastPage) 156.dp else 52.dp, tween(350), label = "cta"
            )
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .width(buttonWidth)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Brush.linearGradient(listOf(cs.primary, cs.secondary)))
                    .clickable {
                        if (isLastPage) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = isLastPage,
                        enter = fadeIn(tween(300, delayMillis = 150)) + expandHorizontally(),
                        exit = fadeOut(tween(120)) + shrinkHorizontally()
                    ) {
                        Text(
                            text = "Get Started",
                            color = cs.onPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = if (isLastPage) "Get started" else "Next page",
                        tint = cs.onPrimary
                    )
                }
            }
        }
    }
}
