package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ANTAR splash mark — "reading everything from your device":
 * a scan line sweeps the phone screen revealing rows of data, extracted
 * readings flow out along connectors into info cards and a storage chip.
 */
@Composable
private fun SplashMark(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "mark")
    val scan by anim.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing)),
        label = "scan"
    )
    val travel by anim.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "travel"
    )
    val pulse by anim.animateFloat(
        0.18f, 0.42f,
        infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier) {
        val s = size.minDimension / 120f
        fun d(v: Float) = v * s
        val c = Offset(size.width / 2f, size.height / 2f)
        fun p(px: Float, py: Float) = Offset(c.x + d(px - 60f), c.y + d(py - 60f))
        fun roundRect(
            color: androidx.compose.ui.graphics.Color,
            px: Float, py: Float, w: Float, h: Float, r: Float,
            stroke: Float? = null
        ) = drawRoundRect(
            color, p(px, py), androidx.compose.ui.geometry.Size(d(w), d(h)),
            androidx.compose.ui.geometry.CornerRadius(d(r)),
            style = stroke?.let { Stroke(d(it)) } ?: androidx.compose.ui.graphics.drawscope.Fill
        )

        // faint scan ripple
        drawCircle(cs.secondary.copy(alpha = .14f), d(48f), c, style = Stroke(d(1.2f)))

        // antenna emitter (the one pop)
        drawLine(cs.onSurface.copy(alpha = .85f), p(60f, 28f), p(60f, 19f), d(1.5f), StrokeCap.Round)
        drawCircle(cs.tertiary.copy(alpha = pulse), d(5.4f) + d(2f) * pulse, p(60f, 17f))
        drawCircle(cs.tertiary, d(2.4f), p(60f, 17f))

        // phone body + screen
        roundRect(cs.surfaceVariant, 44f, 28f, 32f, 64f, 7f)
        roundRect(cs.secondary, 44f, 28f, 32f, 64f, 7f, stroke = 2.2f)
        roundRect(cs.background, 48f, 34f, 24f, 52f, 4f)

        // scan line position within the screen (y 37 → 82)
        val scanY = 37f + 45f * scan

        // data rows: bright once the scan line has passed them this cycle
        data class ScreenRow(val y: Float, val w: Float, val accent: Boolean, val alpha: Float)
        val rows = listOf(
            ScreenRow(40f, 16f, false, .55f),
            ScreenRow(46.5f, 11f, true, .75f),
            ScreenRow(53f, 14f, false, .4f),
            ScreenRow(59.5f, 16f, false, .5f),
            ScreenRow(66f, 13f, true, .6f),
            ScreenRow(72.5f, 16f, false, .45f),
            ScreenRow(79f, 10f, false, .35f)
        )
        rows.forEach { row ->
            val scanned = row.y + 1.3f < scanY
            val color = if (row.accent) cs.primary else cs.onSurface
            roundRect(
                color.copy(alpha = if (scanned) row.alpha else .08f),
                52f, row.y, row.w, 2.6f, 1.3f
            )
        }

        // scanned glow zone + scan line
        if (scan > 0.02f) {
            drawRect(
                Brush.verticalGradient(
                    listOf(cs.primary.copy(alpha = 0f), cs.primary.copy(alpha = .12f)),
                    startY = p(48f, 34f).y, endY = p(48f, scanY).y
                ),
                topLeft = p(48f, 34f),
                size = androidx.compose.ui.geometry.Size(d(24f), d(scanY - 34f))
            )
        }
        drawLine(
            cs.primary.copy(alpha = .22f), p(49.5f, scanY), p(70.5f, scanY),
            strokeWidth = d(5f), cap = StrokeCap.Round
        )
        drawLine(cs.primary, p(49.5f, scanY), p(70.5f, scanY), d(1.8f), StrokeCap.Round)

        // extracted info card, left (cpu reading)
        roundRect(cs.surfaceVariant.copy(alpha = .85f), 10f, 38f, 24f, 17f, 3.5f)
        roundRect(cs.outlineVariant, 10f, 38f, 24f, 17f, 3.5f, stroke = 1f)
        roundRect(cs.primary.copy(alpha = .8f), 13.5f, 42f, 12f, 2.4f, 1.2f)
        roundRect(cs.onSurface.copy(alpha = .25f), 13.5f, 47.5f, 17f, 2.4f, 1.2f)
        drawLine(cs.onSurface.copy(alpha = .85f), p(44f, 46f), p(34f, 46f), d(1.4f), StrokeCap.Round)

        // extracted info card, right (battery reading)
        roundRect(cs.surfaceVariant.copy(alpha = .85f), 86f, 60f, 24f, 17f, 3.5f)
        roundRect(cs.outlineVariant, 86f, 60f, 24f, 17f, 3.5f, stroke = 1f)
        roundRect(cs.secondary.copy(alpha = .9f), 89.5f, 64f, 14f, 2.4f, 1.2f)
        roundRect(cs.onSurface.copy(alpha = .25f), 89.5f, 69.5f, 10f, 2.4f, 1.2f)
        drawLine(cs.onSurface.copy(alpha = .85f), p(76f, 68f), p(86f, 68f), d(1.4f), StrokeCap.Round)

        // downlink to storage chip
        drawLine(cs.onSurface.copy(alpha = .85f), p(60f, 92f), p(60f, 100f), d(1.4f), StrokeCap.Round)
        roundRect(cs.primary, 56.5f, 100f, 7f, 7f, 1.4f)

        // data dots flowing outward along the three links (staggered)
        val leftT = travel
        val rightT = (travel + 0.33f) % 1f
        val downT = (travel + 0.66f) % 1f
        drawCircle(cs.primary.copy(alpha = 1f - leftT), d(1.7f), p(44f - 10f * leftT, 46f))
        drawCircle(cs.secondary.copy(alpha = 1f - rightT), d(1.7f), p(76f + 10f * rightT, 68f))
        drawCircle(cs.primary.copy(alpha = 1f - downT), d(1.7f), p(60f, 92f + 8f * downT))

        // dust
        drawCircle(cs.primary.copy(alpha = .3f + pulse), d(1.1f), p(28f, 76f))
        drawCircle(cs.secondary.copy(alpha = .9f - pulse), d(1.1f), p(92f, 38f))
    }
}

/** Full-screen animated splash shown while device data loads. */
@Composable
fun AnimatedSplash() {
    val cs = MaterialTheme.colorScheme

    // one-shot entrance
    val appear = remember { Animatable(0f) }
    val subAppear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        subAppear.animateTo(1f, tween(900, delayMillis = 300, easing = FastOutSlowInEasing))
    }

    // shimmer on the loading bar
    val shimmer by rememberInfiniteTransition(label = "shimmer").animateFloat(
        -0.35f, 1f,
        infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing)),
        label = "bar"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(cs.surfaceVariant.copy(alpha = .5f), cs.background),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .size(170.dp)
                    .graphicsLayer {
                        alpha = appear.value
                        scaleX = 0.85f + 0.15f * appear.value
                        scaleY = 0.85f + 0.15f * appear.value
                    }
            ) {
                SplashMark(Modifier.fillMaxSize())
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "ANTAR",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 8.sp,
                color = cs.primary,
                modifier = Modifier.graphicsLayer {
                    alpha = appear.value
                    translationY = (1f - appear.value) * 24.dp.toPx()
                }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "SYSTEM · INTERNALS",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 4.sp,
                color = cs.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { alpha = subAppear.value }
            )
        }

        // loading shimmer bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 56.dp)
                .width(140.dp)
                .height(3.dp)
                .clip(CircleShape)
                .background(cs.outlineVariant.copy(alpha = .4f))
                .graphicsLayer { alpha = subAppear.value }
        ) {
            Box(
                modifier = Modifier
                    .offset(x = 140.dp * shimmer)
                    .width(48.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(cs.primary.copy(alpha = 0f), cs.primary, cs.primary.copy(alpha = 0f))
                        )
                    )
            )
        }
    }
}
