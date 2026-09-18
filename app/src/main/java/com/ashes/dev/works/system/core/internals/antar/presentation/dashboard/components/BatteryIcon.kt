package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.ambientFloat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery

// ── Battery Icon ─────────────────────────────────────────────────────

@Composable
internal fun BatteryIcon(
    isCharging: Boolean,
    batteryLevel: Float,
    modifier: Modifier = Modifier,
    batteryColor: Color = if (isCharging) AntarGreen else AntarCyan
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(4.dp)) {
        // The pulse exists only while charging, and its value is read inside the Canvas draw
        // lambda, so it never recomposes the card.
        val pulse = if (isCharging && LocalAnimationIntensity.current != AnimationIntensity.LOW) {
            rememberInfiniteTransition(label = "battery").ambientFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(AntarMotion.AMBIENT_QUICK_MS, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "pulse"
            )
        } else {
            null
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 2.5.dp.toPx()
            val cornerRadiusPx = 4.dp.toPx()
            val terminalHeightPx = 4.dp.toPx()
            val shellWidth = size.width
            val shellHeight = size.height - terminalHeightPx

            drawRoundRect(
                color = batteryColor.copy(alpha = 0.6f),
                topLeft = Offset(0f, terminalHeightPx),
                size = Size(shellWidth, shellHeight),
                style = Stroke(width = strokeWidthPx),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )

            val maxFillWidth = shellWidth - (strokeWidthPx * 2) - 4.dp.toPx()
            val maxFillHeight = shellHeight - (strokeWidthPx * 2) - 4.dp.toPx()
            val fillHeight = maxFillHeight * (batteryLevel / 100f)
            val fillTop = terminalHeightPx + shellHeight - fillHeight - strokeWidthPx - 2.dp.toPx()
            val fillLeft = strokeWidthPx + 2.dp.toPx()

            drawRoundRect(
                color = batteryColor.copy(alpha = pulse?.value ?: 1f),
                topLeft = Offset(fillLeft, fillTop),
                size = Size(maxFillWidth, fillHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            val terminalWidth = shellWidth * 0.4f
            drawRoundRect(
                color = batteryColor.copy(alpha = 0.6f),
                topLeft = Offset((shellWidth - terminalWidth) / 2, 0f),
                size = Size(terminalWidth, terminalHeightPx),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }

        if (isCharging) {
            Icon(
                imageVector = Icons.Filled.BatteryChargingFull,
                contentDescription = stringResource(R.string.dashboard_charging),
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}
