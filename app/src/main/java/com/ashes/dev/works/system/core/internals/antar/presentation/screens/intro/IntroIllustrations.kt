package com.ashes.dev.works.system.core.internals.antar.presentation.screens.intro

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hand-drawn neon-spot illustrations for the intro pager, ported from the
 * design SVGs (design-illustrations/). All geometry lives on a 240x200 grid
 * and is scaled to the composable size; colors come from the Material theme
 * so the artwork adapts to light / dark / dynamic color.
 */

private const val GRID_W = 240f
private const val GRID_H = 200f

private class Grid(val s: Float, val ox: Float, val oy: Float) {
    fun x(v: Float) = ox + v * s
    fun y(v: Float) = oy + v * s
    fun d(v: Float) = v * s
    fun p(px: Float, py: Float) = Offset(x(px), y(py))
}

private fun DrawScope.grid(): Grid {
    val s = minOf(size.width / GRID_W, size.height / GRID_H)
    return Grid(s, (size.width - GRID_W * s) / 2f, (size.height - GRID_H * s) / 2f)
}

private fun DrawScope.backdrop(g: Grid, color: Color) {
    drawCircle(color, radius = g.d(78f), center = g.p(120f, 100f))
}

private fun DrawScope.nodeChip(
    g: Grid,
    from: Offset,
    to: Offset,
    chipTopLeft: Offset,
    color: Color,
    connector: Color,
    haloAlpha: Float
) {
    drawLine(connector, from, to, strokeWidth = g.d(1.4f), cap = StrokeCap.Round)
    drawCircle(color.copy(alpha = haloAlpha), radius = g.d(5f), center = from)
    drawCircle(color, radius = g.d(2.1f), center = from)
    drawRoundRect(
        color, topLeft = chipTopLeft, size = Size(g.d(7f), g.d(7f)),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(g.d(1f))
    )
}

private fun DrawScope.dust(g: Grid, px: Float, py: Float, color: Color, alpha: Float) {
    drawCircle(color.copy(alpha = alpha), radius = g.d(1.1f), center = g.p(px, py))
}

/** Page 1 — phone with live dashboard, node links out to the data. */
@Composable
fun DeviceIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "device")
    val gauge by anim.animateFloat(
        0.3f, 0.85f,
        infiniteRepeatable(tween(1700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "gauge"
    )
    val pulse by anim.animateFloat(
        0.18f, 0.42f,
        infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier) {
        val g = grid()
        backdrop(g, cs.surfaceVariant.copy(alpha = 0.45f))

        // phone body + screen
        drawRoundRect(
            cs.surfaceVariant, topLeft = g.p(86f, 42f), size = Size(g.d(68f), g.d(118f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(g.d(12f))
        )
        drawRoundRect(
            cs.secondary, topLeft = g.p(86f, 42f), size = Size(g.d(68f), g.d(118f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(g.d(12f)),
            style = Stroke(g.d(2.5f))
        )
        drawRoundRect(
            cs.background, topLeft = g.p(92f, 52f), size = Size(g.d(56f), g.d(98f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(g.d(6f))
        )

        // screen cue: window dots + faded bar
        drawCircle(cs.secondary, g.d(1.2f), g.p(98f, 58f))
        drawCircle(cs.secondary, g.d(1.2f), g.p(102f, 58f))
        drawCircle(cs.secondary, g.d(1.2f), g.p(106f, 58f))
        drawLine(
            cs.primary.copy(alpha = 0.5f), g.p(112f, 58f), g.p(140f, 58f),
            strokeWidth = g.d(1.5f), cap = StrokeCap.Round
        )

        // on-screen gauge (animated sweep)
        val gaugeTL = g.p(106f, 78f)
        val gaugeSize = Size(g.d(28f), g.d(28f))
        drawArc(
            cs.outlineVariant, 180f, 180f, useCenter = false,
            topLeft = gaugeTL, size = gaugeSize, style = Stroke(g.d(3.5f), cap = StrokeCap.Round)
        )
        val sweep = 180f * gauge
        drawArc(
            cs.primary, 180f, sweep, useCenter = false,
            topLeft = gaugeTL, size = gaugeSize, style = Stroke(g.d(3.5f), cap = StrokeCap.Round)
        )
        val tip = Math.toRadians((180f + sweep).toDouble())
        drawCircle(
            cs.primary, g.d(2.2f),
            Offset(g.x(120f) + g.d(14f) * cos(tip).toFloat(), g.y(92f) + g.d(14f) * sin(tip).toFloat())
        )

        // ui rows
        val row = androidx.compose.ui.geometry.CornerRadius(g.d(2.5f))
        drawRoundRect(cs.onSurface.copy(alpha = .2f), g.p(100f, 106f), Size(g.d(40f), g.d(5f)), row)
        drawRoundRect(cs.onSurface.copy(alpha = .15f), g.p(100f, 116f), Size(g.d(28f), g.d(5f)), row)
        drawRoundRect(cs.onSurface.copy(alpha = .1f), g.p(100f, 126f), Size(g.d(34f), g.d(5f)), row)
        drawRoundRect(cs.primary.copy(alpha = .5f), g.p(100f, 136f), Size(g.d(22f), g.d(5f)), row)

        // node links (accent = tertiary, exactly one)
        val connector = cs.onSurface.copy(alpha = .85f)
        nodeChip(g, g.p(154f, 84f), g.p(182f, 70f), g.p(182f, 66f), cs.tertiary, connector, pulse)
        nodeChip(g, g.p(154f, 122f), g.p(178f, 134f), g.p(178f, 130.5f), cs.primary, connector, pulse * 0.8f)

        dust(g, 62f, 70f, cs.primary, 0.3f + pulse)
        dust(g, 70f, 120f, cs.secondary, 0.9f - pulse)
        dust(g, 178f, 104f, cs.onSurface, 0.4f + pulse * 0.5f)
        dust(g, 58f, 95f, cs.secondary, 0.5f + pulse)
    }
}

/** Page 2 — SoC chip with traces and breathing core. */
@Composable
fun ChipIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "chip")
    val pulse by anim.animateFloat(
        0.15f, 0.35f,
        infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val bars by anim.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bars"
    )

    Canvas(modifier) {
        val g = grid()
        backdrop(g, cs.surfaceVariant.copy(alpha = 0.45f))

        // traces + square endpoint chips
        val connector = cs.onSurface.copy(alpha = .85f)
        val traceW = g.d(1.4f)
        drawLine(connector, g.p(120f, 68f), g.p(120f, 46f), traceW, StrokeCap.Round)
        drawLine(connector, g.p(120f, 132f), g.p(120f, 154f), traceW, StrokeCap.Round)
        drawLine(connector, g.p(88f, 100f), g.p(64f, 100f), traceW, StrokeCap.Round)
        drawLine(connector, g.p(152f, 100f), g.p(176f, 100f), traceW, StrokeCap.Round)
        drawLine(connector, g.p(97f, 77f), g.p(80f, 60f), traceW, StrokeCap.Round)
        drawLine(connector, g.p(143f, 123f), g.p(160f, 140f), traceW, StrokeCap.Round)
        val chip = Size(g.d(7f), g.d(7f))
        val r1 = androidx.compose.ui.geometry.CornerRadius(g.d(1f))
        drawRoundRect(cs.primary, g.p(116.5f, 39f), chip, r1)
        drawRoundRect(cs.secondary, g.p(116.5f, 154f), chip, r1)
        drawRoundRect(cs.secondary, g.p(57f, 96.5f), chip, r1)
        drawRoundRect(cs.primary, g.p(176f, 96.5f), chip, r1)
        drawRoundRect(cs.primary, g.p(73.5f, 53.5f), chip, r1)
        drawRoundRect(cs.secondary, g.p(156.5f, 136.5f), chip, r1)

        // chip body + die + grid
        drawRoundRect(
            cs.surfaceVariant, g.p(88f, 68f), Size(g.d(64f), g.d(64f)),
            androidx.compose.ui.geometry.CornerRadius(g.d(9f))
        )
        drawRoundRect(
            cs.secondary, g.p(88f, 68f), Size(g.d(64f), g.d(64f)),
            androidx.compose.ui.geometry.CornerRadius(g.d(9f)), style = Stroke(g.d(2.5f))
        )
        drawRoundRect(
            cs.background, g.p(99f, 79f), Size(g.d(42f), g.d(42f)),
            androidx.compose.ui.geometry.CornerRadius(g.d(5f))
        )
        val gridLine = cs.outlineVariant
        drawLine(gridLine, g.p(99f, 93f), g.p(141f, 93f), g.d(1.4f))
        drawLine(gridLine, g.p(99f, 107f), g.p(141f, 107f), g.d(1.4f))
        drawLine(gridLine, g.p(113f, 79f), g.p(113f, 121f), g.d(1.4f))
        drawLine(gridLine, g.p(127f, 79f), g.p(127f, 121f), g.d(1.4f))

        // the one pop: breathing tertiary core
        drawCircle(cs.tertiary.copy(alpha = pulse), g.d(10f) + g.d(3f) * pulse, g.p(120f, 100f))
        drawCircle(cs.tertiary, g.d(4.2f), g.p(120f, 100f))

        // signal bars (animated height, top fade)
        val barBottom = g.y(148f)
        val h1 = g.d(24f + 6f * bars)
        val h2 = g.d(16f + 8f * (1f - bars))
        drawLine(
            Brush.verticalGradient(
                listOf(cs.secondary, cs.secondary.copy(alpha = 0f)),
                startY = barBottom - h1, endY = barBottom
            ),
            Offset(g.x(44f), barBottom - h1), Offset(g.x(44f), barBottom),
            strokeWidth = g.d(2f), cap = StrokeCap.Round
        )
        drawLine(
            Brush.verticalGradient(
                listOf(cs.primary, cs.primary.copy(alpha = 0f)),
                startY = barBottom - h2, endY = barBottom
            ),
            Offset(g.x(52f), barBottom - h2), Offset(g.x(52f), barBottom),
            strokeWidth = g.d(2f), cap = StrokeCap.Round
        )

        dust(g, 186f, 60f, cs.primary, 0.3f + bars * 0.5f)
        dust(g, 196f, 130f, cs.secondary, 0.8f - bars * 0.5f)
        dust(g, 58f, 80f, cs.onSurface, 0.35f + bars * 0.3f)
    }
}

/** Page 3 — radar with rotating sweep + live waveform. */
@Composable
fun SensorsIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "sensors")
    val sweep by anim.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(3600, easing = LinearEasing)),
        label = "sweep"
    )
    val pulse by anim.animateFloat(
        0.2f, 0.45f,
        infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier) {
        val g = grid()
        backdrop(g, cs.surfaceVariant.copy(alpha = 0.45f))

        // radar dial
        val c = g.p(100f, 92f)
        drawCircle(cs.background, g.d(40f), c)
        drawCircle(cs.secondary, g.d(40f), c, style = Stroke(g.d(2f)))
        drawCircle(cs.secondary.copy(alpha = .6f), g.d(26f), c, style = Stroke(g.d(1.2f)))
        drawCircle(cs.secondary.copy(alpha = .4f), g.d(13f), c, style = Stroke(g.d(1f)))
        drawLine(cs.secondary.copy(alpha = .3f), g.p(100f, 52f), g.p(100f, 132f), g.d(1f))
        drawLine(cs.secondary.copy(alpha = .3f), g.p(60f, 92f), g.p(140f, 92f), g.d(1f))

        // rotating sweep wedge + leading edge
        rotate(sweep, pivot = c) {
            drawArc(
                Brush.sweepGradient(
                    0f to cs.primary.copy(alpha = 0f),
                    0.12f to cs.primary.copy(alpha = .3f),
                    0.125f to Color.Transparent,
                    1f to Color.Transparent,
                    center = c
                ),
                startAngle = -45f, sweepAngle = 45f, useCenter = true,
                topLeft = Offset(c.x - g.d(40f), c.y - g.d(40f)),
                size = Size(g.d(80f), g.d(80f))
            )
            val edge = Math.toRadians(0.0)
            drawLine(
                cs.primary, c,
                Offset(c.x + g.d(40f) * cos(edge).toFloat(), c.y + g.d(40f) * sin(edge).toFloat()),
                strokeWidth = g.d(2f), cap = StrokeCap.Round
            )
        }
        drawCircle(cs.primary, g.d(2.6f), c)

        // the one pop: tertiary contact blip
        drawCircle(cs.tertiary.copy(alpha = pulse), g.d(5.6f) + g.d(2f) * pulse, g.p(116f, 68f))
        drawCircle(cs.tertiary, g.d(2.4f), g.p(116f, 68f))
        drawCircle(cs.primary.copy(alpha = .8f), g.d(1.8f), g.p(84f, 108f))
        drawCircle(cs.primary.copy(alpha = .5f), g.d(1.5f), g.p(112f, 112f))

        // waveform with glow underlay, square chip end
        val wave = Path().apply {
            moveTo(g.x(140f), g.y(120f)); lineTo(g.x(154f), g.y(120f))
            lineTo(g.x(160f), g.y(106f)); lineTo(g.x(168f), g.y(134f))
            lineTo(g.x(174f), g.y(120f)); lineTo(g.x(196f), g.y(120f))
        }
        drawPath(wave, cs.primary.copy(alpha = .15f), style = Stroke(g.d(6f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(wave, cs.primary, style = Stroke(g.d(2.5f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawRoundRect(
            cs.secondary, g.p(196f, 116.5f), Size(g.d(7f), g.d(7f)),
            androidx.compose.ui.geometry.CornerRadius(g.d(1f))
        )

        dust(g, 170f, 56f, cs.primary, 0.3f + pulse)
        dust(g, 182f, 88f, cs.secondary, 0.9f - pulse)
        dust(g, 56f, 130f, cs.onSurface, 0.4f + pulse * 0.5f)
        dust(g, 70f, 148f, cs.secondary, 0.5f + pulse)
    }
}

/** Page 4 — privacy shield with a check that draws itself on. */
@Composable
fun PrivacyIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "privacy")
    val check by anim.animateFloat(
        0f, 1f,
        infiniteRepeatable(
            keyframes {
                durationMillis = 2800
                0f at 0
                0f at 300
                1f at 1100 using FastOutSlowInEasing
                1f at 2800
            }
        ),
        label = "check"
    )
    val pulse by anim.animateFloat(
        0.12f, 0.26f,
        infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier) {
        val g = grid()
        backdrop(g, cs.surfaceVariant.copy(alpha = 0.45f))

        // outer shield
        val shield = Path().apply {
            moveTo(g.x(120f), g.y(44f)); lineTo(g.x(164f), g.y(60f)); lineTo(g.x(164f), g.y(104f))
            cubicTo(g.x(164f), g.y(130f), g.x(145f), g.y(148f), g.x(120f), g.y(158f))
            cubicTo(g.x(95f), g.y(148f), g.x(76f), g.y(130f), g.x(76f), g.y(104f))
            lineTo(g.x(76f), g.y(60f)); close()
        }
        drawPath(shield, cs.surfaceVariant)
        drawPath(shield, cs.secondary, style = Stroke(g.d(2.5f), join = StrokeJoin.Round))

        // inner shield (inverted depth: darkest at the focus)
        val inner = Path().apply {
            moveTo(g.x(120f), g.y(56f)); lineTo(g.x(152f), g.y(68f)); lineTo(g.x(152f), g.y(103f))
            cubicTo(g.x(152f), g.y(123f), g.x(138f), g.y(137f), g.x(120f), g.y(145f))
            cubicTo(g.x(102f), g.y(137f), g.x(88f), g.y(123f), g.x(88f), g.y(103f))
            lineTo(g.x(88f), g.y(68f)); close()
        }
        drawPath(inner, cs.background)

        // the one pop: tertiary glow + self-drawing check
        drawCircle(cs.tertiary.copy(alpha = pulse), g.d(22f), g.p(120f, 100f))
        val checkPath = Path().apply {
            moveTo(g.x(106f), g.y(100f)); lineTo(g.x(116f), g.y(110f)); lineTo(g.x(136f), g.y(88f))
        }
        if (check > 0.01f) {
            val pm = PathMeasure().apply { setPath(checkPath, false) }
            val seg = Path()
            pm.getSegment(0f, pm.length * check, seg, true)
            drawPath(seg, cs.tertiary, style = Stroke(g.d(5f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        // orbit nodes
        val connector = cs.onSurface.copy(alpha = .85f)
        nodeChip(g, g.p(164f, 76f), g.p(186f, 64f), g.p(186f, 60f), cs.primary, connector, pulse + 0.1f)
        nodeChip(g, g.p(76f, 116f), g.p(54f, 128f), g.p(48f, 124.5f), cs.secondary, connector, pulse + 0.05f)

        dust(g, 62f, 64f, cs.primary, 0.3f + pulse * 2f)
        dust(g, 184f, 110f, cs.secondary, 0.8f - pulse)
        dust(g, 150f, 162f, cs.onSurface, 0.35f + pulse)
    }
}
