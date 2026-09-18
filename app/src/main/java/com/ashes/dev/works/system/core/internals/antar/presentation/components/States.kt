package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.shimmer

object GradientProgressBarDefaults {
    val Height: Dp = 8.dp

    @Composable
    fun colors(): List<Color> = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)

    @Composable
    fun trackColor(): Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
}

/** A rounded determinate bar whose fill is a horizontal gradient. [progress] is clamped to 0..1. */
@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = GradientProgressBarDefaults.Height,
    colors: List<Color> = GradientProgressBarDefaults.colors(),
    trackColor: Color = GradientProgressBarDefaults.trackColor()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(Brush.horizontalGradient(colors))
        )
    }
}

object LoadingSkeletonDefaults {
    val Shape: Shape = RoundedCornerShape(20.dp)
    val HeaderHeight: Dp = 104.dp
    val SectionHeight: Dp = 180.dp
}

/**
 * Shimmering placeholder cards in the shape of an info screen: a header card and [sections]
 * section cards. Use it for every content-shaped load instead of a bare spinner.
 */
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    sections: Int = 3,
    shape: Shape = LoadingSkeletonDefaults.Shape
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LoadingSkeletonDefaults.HeaderHeight)
                .clip(shape)
                .shimmer()
        )
        repeat(sections) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LoadingSkeletonDefaults.SectionHeight)
                    .clip(shape)
                    .shimmer()
            )
        }
    }
}

object ErrorStateDefaults {
    val Icon: ImageVector = Icons.Outlined.ErrorOutline

    @Composable
    fun iconTint(): Color = MaterialTheme.colorScheme.error

    /** The standard [ErrorState] action: a filled button with press feedback. */
    @Composable
    fun RetryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        Button(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = modifier.pressScale(interactionSource)
        ) {
            Text(text = label)
        }
    }
}

/**
 * Full-screen error: an icon, [message] and an optional [action] slot, usually
 * [ErrorStateDefaults.RetryButton]. Pass a user-facing message, never raw exception text.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = ErrorStateDefaults.Icon,
    iconTint: Color = ErrorStateDefaults.iconTint(),
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(20.dp))
            action()
        }
    }
}
