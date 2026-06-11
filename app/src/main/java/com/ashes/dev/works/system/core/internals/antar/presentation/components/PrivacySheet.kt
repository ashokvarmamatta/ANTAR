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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarRed

/**
 * "Data stays on your device" mark: phone with on-device data rows,
 * a pulsing shield-check, and a crossed-out cloud. 120x120 design grid.
 */
@Composable
private fun PrivacyIllustration(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val pulse by rememberInfiniteTransition(label = "privacy").animateFloat(
        0.08f, 0.18f,
        infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "halo"
    )

    Canvas(modifier) {
        val u = size.minDimension / 120f
        fun o(x: Float, y: Float) = Offset(x * u, y * u)
        fun stroke(w: Float) = Stroke(w * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun bar(color: Color, x: Float, y: Float, w: Float, alpha: Float) = drawRoundRect(
            color.copy(alpha = alpha), o(x, y), Size(w * u, 2.6f * u), CornerRadius(1.3f * u)
        )

        // ambient ripple
        drawCircle(cs.secondary.copy(alpha = .14f), 46f * u, o(60f, 60f), style = stroke(1.2f))

        // phone
        drawRoundRect(cs.surfaceVariant, o(44f, 30f), Size(32f * u, 60f * u), CornerRadius(7f * u))
        drawRoundRect(cs.secondary, o(44f, 30f), Size(32f * u, 60f * u), CornerRadius(7f * u), style = stroke(2.2f))
        drawRoundRect(cs.background, o(48f, 36f), Size(24f * u, 48f * u), CornerRadius(4f * u))

        // on-device data rows
        bar(cs.primary, 52f, 41f, 16f, .75f)
        bar(cs.primary, 52f, 47.5f, 11f, .45f)
        bar(cs.primary, 52f, 54f, 14f, .6f)
        bar(cs.onSurface, 52f, 60.5f, 16f, .35f)
        bar(cs.onSurface, 52f, 67f, 12f, .25f)

        // shield badge (pulsing halo + solid mark)
        drawCircle(AntarGreen.copy(alpha = pulse), 17f * u, o(78f, 79f))
        val shield = Path().apply {
            moveTo(78f * u, 66f * u)
            lineTo(88f * u, 70f * u); lineTo(88f * u, 78f * u)
            cubicTo(88f * u, 86f * u, 83.5f * u, 91f * u, 78f * u, 93.5f * u)
            cubicTo(72.5f * u, 91f * u, 68f * u, 86f * u, 68f * u, 78f * u)
            lineTo(68f * u, 70f * u)
            close()
        }
        drawPath(shield, cs.background)
        drawPath(shield, AntarGreen, style = stroke(2f))
        val check = Path().apply {
            moveTo(73.5f * u, 78.8f * u)
            lineTo(76.6f * u, 82f * u)
            lineTo(83f * u, 74.8f * u)
        }
        drawPath(check, AntarGreen, style = stroke(2.3f))

        // no-cloud
        val cloud = Path().apply {
            moveTo(82f * u, 38f * u)
            cubicTo(78.5f * u, 33f * u, 82.5f * u, 27.5f * u, 87.5f * u, 28.6f * u)
            cubicTo(89f * u, 24.8f * u, 97f * u, 24.2f * u, 98.4f * u, 30.6f * u)
            cubicTo(103f * u, 31.5f * u, 103.5f * u, 36.5f * u, 99.6f * u, 38f * u)
            close()
        }
        drawPath(cloud, cs.onSurfaceVariant.copy(alpha = .9f), style = stroke(1.8f))
        drawLine(AntarRed.copy(alpha = .9f), o(79.5f, 40.5f), o(101.5f, 25.5f), 2.2f * u, StrokeCap.Round)

        // dust
        drawCircle(cs.primary.copy(alpha = .5f), 1.3f * u, o(30f, 44f))
        drawCircle(AntarPurple.copy(alpha = .75f), 1.4f * u, o(26f, 84f))
        drawCircle(cs.secondary.copy(alpha = .55f), 1.1f * u, o(96f, 58f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySheet(
    onDismiss: () -> Unit,
    onReadPolicy: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = cs.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrivacyIllustration(Modifier.size(150.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Nothing leaves your device",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface
            )
            Text(
                text = "ANTAR has no servers, no analytics, no ads and no accounts.",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            PrivacyPoint(
                icon = Icons.Outlined.Visibility,
                accent = cs.primary,
                title = "What ANTAR reads",
                body = "Device specs, CPU, battery, storage, display, network, sensors, " +
                    "camera capabilities and your installed apps — live from the hardware, " +
                    "shown only to you."
            )
            PrivacyPoint(
                icon = Icons.Outlined.CloudOff,
                accent = AntarGreen,
                title = "What is collected or shared",
                body = "Nothing. No reading is uploaded, logged remotely or sold. " +
                    "The app works fully offline."
            )
            PrivacyPoint(
                icon = Icons.Outlined.Storage,
                accent = AntarPurple,
                title = "What stays stored",
                body = "Only your preferences and battery history, saved locally. " +
                    "Uninstalling the app deletes everything."
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(cs.primary, cs.secondary)))
                    .clickable(onClick = onReadPolicy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Read full privacy policy",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.onPrimary
                )
            }
        }
    }
}

@Composable
private fun PrivacyPoint(
    icon: ImageVector,
    accent: Color,
    title: String,
    body: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
