package com.ashes.dev.works.system.core.internals.antar.presentation.intro

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private data class IntroPage(
    @param:StringRes val title: Int,
    @param:StringRes val description: Int,
    val illustration: @Composable (Modifier) -> Unit
)

private val introPages = listOf(
    IntroPage(R.string.intro_device_title, R.string.intro_device_body) { DeviceIllustration(it) },
    IntroPage(R.string.intro_chip_title, R.string.intro_chip_body) { ChipIllustration(it) },
    IntroPage(R.string.intro_sensors_title, R.string.intro_sensors_body) { SensorsIllustration(it) },
    IntroPage(R.string.intro_privacy_title, R.string.intro_privacy_body) { PrivacyIllustration(it) }
)

@Composable
fun IntroScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { introPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == introPages.lastIndex
    val cs = MaterialTheme.colorScheme
    val intensity = LocalAnimationIntensity.current
    val duration = if (intensity == AnimationIntensity.LOW) 0 else AntarMotion.MEDIUM_MS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .safeDrawingPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            // The illustration scales with the space the window really has, so the page fits a
            // landscape phone, a split-screen half or a floating bubble as well as a tall phone.
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val artSize = minOf(maxWidth * 0.8f, maxHeight * 0.45f, 300.dp)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 32.dp)
                        .heightIn(min = maxHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(artSize)
                            .graphicsLayer {
                                // Parallax drift + fade while swiping, read in the draw phase.
                                if (intensity != AnimationIntensity.LOW) {
                                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    translationX = pageOffset * size.width * 0.25f
                                    alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f) * 0.6f
                                }
                            }
                    ) {
                        introPages[page].illustration(Modifier.fillMaxSize())
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = stringResource(introPages[page].title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = cs.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 520.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = stringResource(introPages[page].description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 520.dp)
                    )
                }
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
                if (isLastPage) 0f else 1f, tween(duration), label = "skip"
            )
            val skipSource = remember { MutableInteractionSource() }
            TextButton(
                onClick = onFinish,
                enabled = !isLastPage,
                interactionSource = skipSource,
                modifier = Modifier
                    .alpha(skipAlpha)
                    .pressScale(skipSource)
            ) {
                Text(stringResource(R.string.intro_skip), color = cs.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(introPages.size) { index ->
                    val selected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        if (selected) 26.dp else 8.dp, tween(duration), label = "dot$index"
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
                if (isLastPage) 156.dp else 52.dp, tween(duration), label = "cta"
            )
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .width(buttonWidth)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Brush.linearGradient(listOf(cs.primary, cs.secondary)))
                    .bounceClick {
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
                        enter = fadeIn(tween(duration)) + expandHorizontally(),
                        exit = fadeOut(tween(if (duration == 0) 0 else AntarMotion.FAST_MS)) + shrinkHorizontally()
                    ) {
                        Text(
                            text = stringResource(R.string.intro_get_started),
                            color = cs.onPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = stringResource(if (isLastPage) R.string.intro_get_started else R.string.intro_next_page),
                        tint = cs.onPrimary
                    )
                }
            }
        }
    }
}
