package com.ashes.dev.works.system.core.internals.antar.core.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.spatialSpec

/** Colours of a [GradientHeaderCard]: a soft background wash and a fading border. */
@Immutable
data class GradientHeaderColors(val background: List<Color>, val border: List<Color>)

object GradientHeaderCardDefaults {
    val Shape: Shape = RoundedCornerShape(20.dp)
    val BorderWidth: Dp = 1.dp

    /** Primary, secondary and tertiary of the current colour scheme, washed out behind the content. */
    @Composable
    fun colors(
        start: Color = MaterialTheme.colorScheme.primary,
        middle: Color = MaterialTheme.colorScheme.secondary,
        end: Color = MaterialTheme.colorScheme.tertiary
    ): GradientHeaderColors = GradientHeaderColors(
        background = listOf(start.copy(alpha = 0.15f), middle.copy(alpha = 0.10f), end.copy(alpha = 0.08f)),
        border = listOf(start.copy(alpha = 0.4f), end.copy(alpha = 0.1f))
    )
}

/** A full-width card with a gradient wash, for the hero block at the top of a screen. */
@Composable
fun GradientHeaderCard(
    modifier: Modifier = Modifier,
    colors: GradientHeaderColors = GradientHeaderCardDefaults.colors(),
    shape: Shape = GradientHeaderCardDefaults.Shape,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(colors.background))
            .border(
                width = GradientHeaderCardDefaults.BorderWidth,
                brush = Brush.linearGradient(colors.border),
                shape = shape
            )
    ) {
        content()
    }
}

object PremiumCardDefaults {
    val Shape: Shape = RoundedCornerShape(20.dp)
    val ContentPadding: PaddingValues = PaddingValues(20.dp)
    val BorderWidth: Dp = 0.5.dp

    @Composable
    fun containerColor(): Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    @Composable
    fun borderColor(): Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
}

/** A section card whose height animates when its content grows or shrinks. */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    shape: Shape = PremiumCardDefaults.Shape,
    containerColor: Color = PremiumCardDefaults.containerColor(),
    borderColor: Color = PremiumCardDefaults.borderColor(),
    contentPadding: PaddingValues = PremiumCardDefaults.ContentPadding,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = PremiumCardDefaults.BorderWidth, color = borderColor, shape = shape)
        ) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .animateContentSize(animationSpec = LocalAnimationIntensity.current.spatialSpec())
            ) {
                content()
            }
        }
    }
}
