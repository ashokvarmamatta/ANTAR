package com.ashes.dev.works.system.core.internals.antar.presentation.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec

/** A row of equal segments; tapping one only ticks it (Apply commits). */
@Composable
internal fun <T> SegmentedChoice(
    options: List<Pair<T, Int>>,
    selected: T,
    accent: Color,
    onSelected: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val intensity = LocalAnimationIntensity.current
        options.forEach { (value, label) ->
            val isSelected = selected == value
            val background by animateColorAsState(
                if (isSelected) accent.copy(alpha = 0.15f) else Color.Transparent,
                intensity.effectsSpec(),
                label = "segmentBackground"
            )
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(background)
                    .border(0.5.dp, if (isSelected) accent.copy(alpha = 0.4f) else Color.Transparent, shape)
                    .bounceClick { onSelected(value) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
