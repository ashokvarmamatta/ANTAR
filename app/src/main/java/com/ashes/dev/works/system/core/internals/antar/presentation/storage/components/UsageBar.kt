package com.ashes.dev.works.system.core.internals.antar.presentation.storage.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientProgressBar

/** Fills from empty to [fraction] when first shown; instant on low animation intensity. */
@Composable
internal fun UsageBar(fraction: Float, percentText: String, colors: List<Color>, accent: Color) {
    val intensity = LocalAnimationIntensity.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(fraction, intensity) {
        progress.animateTo(fraction.coerceIn(0f, 1f), intensity.effectsSpec())
    }

    Spacer(modifier = Modifier.height(8.dp))
    GradientProgressBar(progress = progress.value, height = 8.dp, colors = colors)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = percentText,
        style = MaterialTheme.typography.labelSmall,
        color = accent,
        fontWeight = FontWeight.Bold
    )
}
