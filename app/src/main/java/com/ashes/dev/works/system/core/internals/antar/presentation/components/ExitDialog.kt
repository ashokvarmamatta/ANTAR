package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/** Animated power glyph for the exit dialog (120x120 design grid). */
@Composable
private fun PowerIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val anim = rememberInfiniteTransition(label = "power")
    val pulse by anim.animateFloat(
        0.15f, 0.4f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier) {
        val s = size.minDimension / 120f
        fun d(v: Float) = v * s
        val c = Offset(size.width / 2f, size.height / 2f)

        drawCircle(cs.surfaceVariant.copy(alpha = .5f), d(44f), c)
        drawCircle(cs.surfaceVariant, d(34f), c)
        drawCircle(cs.outlineVariant, d(34f), c, style = Stroke(d(2f)))

        // power glyph: 270° arc + stem, soft glow behind
        val arcTL = Offset(c.x - d(18f), c.y - d(18f))
        val arcSize = androidx.compose.ui.geometry.Size(d(36f), d(36f))
        drawArc(
            cs.primary.copy(alpha = pulse * 0.6f), 225f, -270f, useCenter = false,
            topLeft = arcTL, size = arcSize, style = Stroke(d(9f), cap = StrokeCap.Round)
        )
        drawArc(
            cs.primary, 225f, -270f, useCenter = false,
            topLeft = arcTL, size = arcSize, style = Stroke(d(4.5f), cap = StrokeCap.Round)
        )
        drawLine(
            cs.primary, Offset(c.x, c.y - d(22f)), Offset(c.x, c.y - d(4f)),
            strokeWidth = d(4.5f), cap = StrokeCap.Round
        )

        // the one pop: tertiary node at the stem tip
        drawCircle(cs.tertiary.copy(alpha = pulse), d(6.5f) + d(2f) * pulse, Offset(c.x, c.y - d(22f)))
        drawCircle(cs.tertiary, d(2.6f), Offset(c.x, c.y - d(22f)))

        // ambient dust
        drawCircle(cs.primary.copy(alpha = .3f + pulse), d(1.1f), Offset(c.x - d(30f), c.y - d(22f)))
        drawCircle(cs.secondary.copy(alpha = .9f - pulse), d(1.1f), Offset(c.x + d(30f), c.y - d(14f)))
        drawCircle(cs.onSurface.copy(alpha = .35f + pulse), d(1.1f), Offset(c.x + d(24f), c.y + d(26f)))
    }
}

/** Modern exit confirmation dialog — illustration, rounded card, gradient CTA. */
@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = cs.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PowerIllustration(Modifier.size(130.dp))

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Leave ANTAR?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Your device stats will be right here\nwhen you come back.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(26.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Stay", fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Brush.linearGradient(listOf(cs.primary, cs.secondary)))
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Exit", color = cs.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
