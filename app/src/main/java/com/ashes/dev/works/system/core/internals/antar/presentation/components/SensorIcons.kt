package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPink
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarTeal

/**
 * Hand-drawn monoline glyphs for each Android sensor family, on a 24x24 design
 * grid: neutral structure stroke + one accent element per icon.
 */
enum class SensorGlyph {
    Accelerometer, Gyroscope, Magnetic, Light, Proximity, Pressure, Gravity,
    Rotation, Steps, Heart, Temperature, Humidity, Motion, Node
}

fun sensorGlyphFor(type: String): SensorGlyph {
    val t = type.lowercase()
    return when {
        "acceler" in t -> SensorGlyph.Accelerometer
        "gyroscope" in t -> SensorGlyph.Gyroscope
        "magnetic" in t -> SensorGlyph.Magnetic
        "light" in t -> SensorGlyph.Light
        "proximity" in t -> SensorGlyph.Proximity
        "pressure" in t -> SensorGlyph.Pressure
        "gravity" in t -> SensorGlyph.Gravity
        "rotation" in t || "orientation" in t -> SensorGlyph.Rotation
        "step" in t -> SensorGlyph.Steps
        "heart" in t -> SensorGlyph.Heart
        "temperature" in t -> SensorGlyph.Temperature
        "humidity" in t -> SensorGlyph.Humidity
        "motion" in t || "stationary" in t -> SensorGlyph.Motion
        else -> SensorGlyph.Node
    }
}

/** Brand accent per sensor family so the list reads grouped, not recolored. */
val SensorGlyph.accent: Color
    get() = when (this) {
        SensorGlyph.Accelerometer, SensorGlyph.Proximity,
        SensorGlyph.Motion, SensorGlyph.Node -> AntarCyan
        SensorGlyph.Gyroscope, SensorGlyph.Rotation -> AntarBlue
        SensorGlyph.Magnetic, SensorGlyph.Gravity -> AntarPurple
        SensorGlyph.Light, SensorGlyph.Temperature -> AntarOrange
        SensorGlyph.Pressure, SensorGlyph.Humidity -> AntarTeal
        SensorGlyph.Steps -> AntarGreen
        SensorGlyph.Heart -> AntarPink
    }

@Composable
fun SensorTypeIcon(
    glyph: SensorGlyph,
    modifier: Modifier = Modifier,
    accent: Color = glyph.accent
) {
    if (glyph == SensorGlyph.Node) {
        // Fallback/unknown sensors reuse the same mark as the Sensors tab chip.
        Icon(
            imageVector = Icons.Outlined.Sensors,
            contentDescription = null,
            modifier = modifier,
            tint = accent
        )
        return
    }
    val structure = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
    Canvas(modifier) { drawSensorGlyph(glyph, structure, accent) }
}

private fun DrawScope.drawSensorGlyph(glyph: SensorGlyph, structure: Color, accent: Color) {
    val u = size.minDimension / 24f
    val sw = 1.8f * u
    fun o(x: Float, y: Float) = Offset(x * u, y * u)
    fun stroke(w: Float = sw) = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round)
    fun line(c: Color, x1: Float, y1: Float, x2: Float, y2: Float, w: Float = sw, a: Float = 1f) =
        drawLine(c.copy(alpha = c.alpha * a), o(x1, y1), o(x2, y2), w, StrokeCap.Round)
    fun polyline(c: Color, vararg pts: Float, w: Float = sw) {
        val p = Path().apply {
            moveTo(pts[0] * u, pts[1] * u)
            for (i in 2 until pts.size step 2) lineTo(pts[i] * u, pts[i + 1] * u)
        }
        drawPath(p, c, style = stroke(w))
    }
    fun arc(c: Color, cx: Float, cy: Float, r: Float, start: Float, sweep: Float, w: Float = sw, a: Float = 1f) =
        drawArc(
            c.copy(alpha = c.alpha * a), start, sweep, useCenter = false,
            topLeft = o(cx - r, cy - r), size = Size(2 * r * u, 2 * r * u), style = stroke(w)
        )

    when (glyph) {
        SensorGlyph.Accelerometer -> {
            drawCircle(structure, 1.7f * u, o(12f, 12f))
            line(accent, 12f, 12f, 19.6f, 12f); polyline(accent, 17.4f, 9.9f, 19.8f, 12f, 17.4f, 14.1f)
            line(accent, 12f, 12f, 12f, 4.4f); polyline(accent, 9.9f, 6.6f, 12f, 4.2f, 14.1f, 6.6f)
            line(accent, 12f, 12f, 6.4f, 17.6f); polyline(accent, 9.2f, 17.6f, 6.2f, 17.8f, 6.4f, 14.8f)
        }
        SensorGlyph.Gyroscope -> {
            drawCircle(structure, 7.4f * u, o(12f, 12f), style = stroke())
            rotate(-28f, o(12f, 12f)) {
                drawOval(accent, o(2f, 8.5f), Size(20f * u, 7f * u), alpha = .9f, style = stroke())
            }
            drawCircle(accent, 1.4f * u, o(20.8f, 7.3f))
        }
        SensorGlyph.Magnetic -> {
            val horseshoe = Path().apply {
                moveTo(8f * u, 4.5f * u); lineTo(8f * u, 13f * u)
                arcTo(Rect(o(8f, 9f), o(16f, 17f)), 180f, -180f, false)
                lineTo(16f * u, 4.5f * u)
            }
            drawPath(horseshoe, structure, style = stroke())
            line(accent, 6.9f, 6.3f, 9.1f, 6.3f); line(accent, 14.9f, 6.3f, 17.1f, 6.3f)
            val field = Path().apply {
                moveTo(4.4f * u, 11.5f * u)
                quadraticBezierTo(12f * u, 18.8f * u, 19.6f * u, 11.5f * u)
            }
            drawPath(field, accent.copy(alpha = .75f), style = stroke())
        }
        SensorGlyph.Light -> {
            drawCircle(structure, 3.4f * u, o(12f, 12f), style = stroke())
            drawCircle(accent, 1.1f * u, o(12f, 12f))
            line(accent, 17.6f, 12f, 20.4f, 12f); line(accent, 3.6f, 12f, 6.4f, 12f)
            line(accent, 12f, 6.4f, 12f, 3.6f); line(accent, 12f, 17.6f, 12f, 20.4f)
            line(accent, 15.96f, 8.04f, 17.94f, 6.06f); line(accent, 8.04f, 8.04f, 6.06f, 6.06f)
            line(accent, 15.96f, 15.96f, 17.94f, 17.94f); line(accent, 8.04f, 15.96f, 6.06f, 17.94f)
        }
        SensorGlyph.Proximity -> {
            line(structure, 19.6f, 5.5f, 19.6f, 18.5f, w = 2.2f * u)
            drawCircle(accent, 1.6f * u, o(5.3f, 12f))
            arc(accent, 5.3f, 12f, 3.6f, -50f, 100f)
            arc(accent, 5.3f, 12f, 6.3f, -50f, 100f, a = .7f)
            arc(accent, 5.3f, 12f, 9f, -50f, 100f, a = .45f)
        }
        SensorGlyph.Pressure -> {
            arc(structure, 12f, 13f, 8f, 154f, 232f)
            line(structure, 6.34f, 8.34f, 7.62f, 9.62f, w = 1.4f * u, a = .8f)
            line(structure, 12f, 6f, 12f, 7.8f, w = 1.4f * u, a = .8f)
            line(structure, 17.66f, 8.34f, 16.38f, 9.62f, w = 1.4f * u, a = .8f)
            line(accent, 12f, 14f, 16.6f, 10.1f, w = 2f * u)
            drawCircle(accent, 1.7f * u, o(12f, 14f))
        }
        SensorGlyph.Gravity -> {
            line(accent, 12f, 4.2f, 12f, 14.4f, w = 2f * u)
            polyline(accent, 8.8f, 11.6f, 12f, 14.8f, 15.2f, 11.6f, w = 2f * u)
            val ground = Path().apply {
                moveTo(4f * u, 19.8f * u)
                quadraticBezierTo(12f * u, 16.4f * u, 20f * u, 19.8f * u)
            }
            drawPath(ground, structure, style = stroke(2f * u))
        }
        SensorGlyph.Rotation -> {
            drawRoundRect(
                structure, o(8.5f, 8.5f), Size(7f * u, 7f * u),
                CornerRadius(1.4f * u), style = stroke()
            )
            arc(accent, 12f, 12f, 8.2f, 270f, -250f)
            polyline(accent, 20.2f, 17.7f, 19.7f, 14.8f, 17.4f, 16.7f)
        }
        SensorGlyph.Steps -> {
            rotate(12f, o(7.1f, 7.4f)) {
                drawRoundRect(
                    structure, o(5f, 3.8f), Size(4.2f * u, 7.2f * u),
                    CornerRadius(2.1f * u), style = stroke(1.6f * u)
                )
            }
            drawCircle(structure, 1.2f * u, o(5.6f, 13.6f))
            rotate(-12f, o(16.9f, 13f)) {
                drawRoundRect(
                    accent, o(14.8f, 9.4f), Size(4.2f * u, 7.2f * u),
                    CornerRadius(2.1f * u), style = stroke(1.6f * u)
                )
            }
            drawCircle(accent, 1.2f * u, o(18.4f, 19.2f))
        }
        SensorGlyph.Heart -> {
            val heart = Path().apply {
                moveTo(12f * u, 19.2f * u)
                cubicTo(7f * u, 15.2f * u, 4.5f * u, 12.2f * u, 4.5f * u, 8.9f * u)
                cubicTo(4.5f * u, 6.1f * u, 6.6f * u, 4.5f * u, 8.8f * u, 4.5f * u)
                cubicTo(10.3f * u, 4.5f * u, 11.4f * u, 5.3f * u, 12f * u, 6.4f * u)
                cubicTo(12.6f * u, 5.3f * u, 13.7f * u, 4.5f * u, 15.2f * u, 4.5f * u)
                cubicTo(17.4f * u, 4.5f * u, 19.5f * u, 6.1f * u, 19.5f * u, 8.9f * u)
                cubicTo(19.5f * u, 12.2f * u, 17f * u, 15.2f * u, 12f * u, 19.2f * u)
                close()
            }
            drawPath(heart, structure, style = stroke(1.7f * u))
            polyline(
                accent, 6.6f, 11.4f, 8.8f, 11.4f, 10.2f, 8.8f,
                12.4f, 14.2f, 13.8f, 11.4f, 17.4f, 11.4f, w = 1.6f * u
            )
        }
        SensorGlyph.Temperature -> {
            val stem = Path().apply {
                moveTo(10.4f * u, 13.6f * u); lineTo(10.4f * u, 5.6f * u)
                arcTo(Rect(o(10.4f, 4f), o(13.6f, 7.2f)), 180f, 180f, false)
                lineTo(13.6f * u, 13.6f * u)
            }
            drawPath(stem, structure, style = stroke(1.7f * u))
            drawCircle(structure, 3.2f * u, o(12f, 16.5f), style = stroke(1.7f * u))
            line(accent, 12f, 9.4f, 12f, 14.4f, w = 1.7f * u)
            drawCircle(accent, 1.8f * u, o(12f, 16.5f))
            line(structure, 16.2f, 6.6f, 18.4f, 6.6f, w = 1.3f * u, a = .8f)
            line(structure, 16.2f, 9.6f, 18.4f, 9.6f, w = 1.3f * u, a = .8f)
        }
        SensorGlyph.Humidity -> {
            val drop = Path().apply {
                moveTo(12f * u, 3.9f * u)
                cubicTo(12f * u, 3.9f * u, 5.9f * u, 10.8f * u, 5.9f * u, 14.8f * u)
                arcTo(Rect(o(5.9f, 8.7f), o(18.1f, 20.9f)), 180f, -180f, false)
                cubicTo(18.1f * u, 10.8f * u, 12f * u, 3.9f * u, 12f * u, 3.9f * u)
                close()
            }
            drawPath(drop, structure, style = stroke(1.7f * u))
            val wave = Path().apply {
                moveTo(8.4f * u, 14.6f * u)
                quadraticBezierTo(10.2f * u, 13.1f * u, 12f * u, 14.6f * u)
                quadraticBezierTo(13.8f * u, 16.1f * u, 15.6f * u, 14.6f * u)
            }
            drawPath(wave, accent, style = stroke(1.6f * u))
        }
        SensorGlyph.Motion -> {
            drawCircle(accent, 2.1f * u, o(16.4f, 12f))
            drawCircle(accent.copy(alpha = .35f), 4f * u, o(16.4f, 12f), style = stroke())
            line(structure, 4f, 8.4f, 10f, 8.4f)
            line(structure, 5.6f, 12f, 11.2f, 12f)
            line(structure, 4f, 15.6f, 10f, 15.6f)
        }
        // Node is rendered as the Material Sensors icon in SensorTypeIcon.
        SensorGlyph.Node -> Unit
    }
}
