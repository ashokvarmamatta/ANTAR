package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec

internal val ChargingGreen = Color(0xFF4CAF50)
internal val ChargingGreenLight = Color(0xFF81C784)
internal val DischargingRed = Color(0xFFF44336)

private val HistoryGridLevels = listOf(0, 25, 50, 75, 100)

/** Stored battery level over time; segments are green while charging and red while discharging. */
@Composable
internal fun BatteryHistoryGraph(
    points: List<HistoryPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val leftPadding = 40.dp.toPx()
        val graphWidth = size.width - leftPadding
        val graphHeight = size.height

        for (pct in HistoryGridLevels) {
            val y = graphHeight - (pct / 100f) * graphHeight
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val widthPerPoint = graphWidth / (points.size - 1)
        val fillPath = Path()
        val segmentStroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)

        fun yAt(index: Int): Float = graphHeight - (points[index].levelPercent / 100f) * graphHeight

        val firstY = yAt(0)
        fillPath.moveTo(leftPadding, graphHeight)
        fillPath.lineTo(leftPadding, firstY)

        val segmentPath = Path()
        for (i in 0 until points.size - 1) {
            val x1 = leftPadding + i * widthPerPoint
            val y1 = yAt(i)
            val x2 = leftPadding + (i + 1) * widthPerPoint
            val y2 = yAt(i + 1)
            val cx = x1 + (x2 - x1) / 2f

            fillPath.cubicTo(cx, y1, cx, y2, x2, y2)

            segmentPath.reset()
            segmentPath.moveTo(x1, y1)
            segmentPath.cubicTo(cx, y1, cx, y2, x2, y2)
            drawPath(
                path = segmentPath,
                color = if (points[i].isCharging) ChargingGreen else DischargingRed,
                style = segmentStroke
            )
        }

        fillPath.lineTo(size.width, graphHeight)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent),
                startY = 0f,
                endY = graphHeight
            )
        )

        val lastX = leftPadding + (points.size - 1) * widthPerPoint
        val lastY = yAt(points.size - 1)
        val endColor = if (points.last().isCharging) ChargingGreen else DischargingRed
        drawCircle(color = endColor, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(color = endColor.copy(alpha = 0.3f), radius = 8.dp.toPx(), center = Offset(lastX, lastY))
    }
}

/** Level ring. [progress] is read in the draw phase, so animating it only redraws. */
@Composable
internal fun CircularBatteryProgress(progress: () -> Float, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val strokeWidthPx = 14.dp.toPx()
        val drawSize = size.minDimension - strokeWidthPx

        drawCircle(
            color = trackColor,
            radius = drawSize / 2,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress().coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset((size.width - drawSize) / 2, (size.height - drawSize) / 2),
            size = Size(drawSize, drawSize),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}

/**
 * Smooth line chart of live samples. The y-range eases between updates; both the range and the
 * points are read inside the draw block, so a new sample redraws without re-measuring.
 */
@Composable
internal fun LineGraph(
    dataPoints: List<Number>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    fixedMin: Float? = null,
    fixedMax: Float? = null,
    showBezier: Boolean = true
) {
    if (dataPoints.isEmpty()) return

    val floatData = remember(dataPoints) { dataPoints.map { it.toFloat() } }
    val maxVal = fixedMax ?: (floatData.maxOrNull() ?: 1f)
    val minVal = fixedMin ?: (floatData.minOrNull() ?: 0f)

    val intensity = LocalAnimationIntensity.current
    val animatedMax by animateFloatAsState(targetValue = maxVal, animationSpec = intensity.effectsSpec(), label = "max")
    val animatedMin by animateFloatAsState(targetValue = minVal, animationSpec = intensity.effectsSpec(), label = "min")

    Canvas(modifier = modifier) {
        val range = (animatedMax - animatedMin).coerceAtLeast(0.0001f)
        clipRect {
            fun yAt(index: Int): Float {
                val normalized = ((floatData[index] - animatedMin) / range).coerceIn(0f, 1f)
                return size.height - normalized * size.height
            }

            if (floatData.size < 2) {
                drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(0f, yAt(0)))
                return@clipRect
            }

            val widthPerPoint = size.width / (floatData.size - 1)
            val path = Path()
            val fillPath = Path()

            val firstY = yAt(0)
            path.moveTo(0f, firstY)
            fillPath.moveTo(0f, size.height)
            fillPath.lineTo(0f, firstY)

            for (i in 0 until floatData.size - 1) {
                val x1 = i * widthPerPoint
                val y1 = yAt(i)
                val x2 = (i + 1) * widthPerPoint
                val y2 = yAt(i + 1)

                if (showBezier) {
                    val cx = x1 + (x2 - x1) / 2f
                    path.cubicTo(cx, y1, cx, y2, x2, y2)
                    fillPath.cubicTo(cx, y1, cx, y2, x2, y2)
                } else {
                    path.lineTo(x2, y2)
                    fillPath.lineTo(x2, y2)
                }
            }

            fillPath.lineTo(size.width, size.height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = size.height
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            val lastY = yAt(floatData.size - 1)
            drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(size.width, lastY))
            drawCircle(color = lineColor.copy(alpha = 0.4f), radius = 8.dp.toPx(), center = Offset(size.width, lastY))
        }
    }
}
