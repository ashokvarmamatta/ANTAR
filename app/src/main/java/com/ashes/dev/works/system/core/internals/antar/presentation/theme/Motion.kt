package com.ashes.dev.works.system.core.internals.antar.presentation.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.animation.core.snap
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * ANTAR motion system.
 *
 * A single, physics-based spring language shared across the app so every transition, press and
 * resize feels consistent and natural (overshoot + settle) instead of a fixed-duration timer.
 * Spatial springs drive size/offset/shape; effect springs drive color/alpha.
 */
object AntarMotion {

    /** Default spatial spring — size/offset/shape. Gentle, slightly bouncy. */
    fun <T> spatial(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)

    /** Snappy spatial spring — for quick press/release feedback. */
    fun <T> fastSpatial(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)

    /** Effects spring — color/alpha. Settled, no bounce. */
    fun <T> effects(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
}

/**
 * How much motion the user wants. LOW makes transitions instant (best for very low-end devices or
 * accessibility), MEDIUM is the calm default, HIGH is the full springy/expressive feel.
 */
enum class AnimationIntensity { LOW, MEDIUM, HIGH }

/** Current animation intensity, provided from user preference at the top of the tree. */
val LocalAnimationIntensity = staticCompositionLocalOf { AnimationIntensity.HIGH }

/**
 * A spatial spec that respects the current [AnimationIntensity]: LOW snaps instantly, MEDIUM uses a
 * calm spring, HIGH uses a bouncier spring. Read inside composition.
 */
fun <T> AnimationIntensity.spatialSpec(): FiniteAnimationSpec<T> = when (this) {
    AnimationIntensity.LOW -> snap()
    AnimationIntensity.MEDIUM -> spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow)
    AnimationIntensity.HIGH -> spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)
}

/** Effects (color/alpha) spec that respects the current [AnimationIntensity]. */
fun <T> AnimationIntensity.effectsSpec(): FiniteAnimationSpec<T> = when (this) {
    AnimationIntensity.LOW -> snap()
    else -> spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
}

/**
 * GPU-cheap "squish" press feedback. Scales the element down while pressed and springs it back on
 * release. Uses [graphicsLayer] so it never triggers relayout. Pair with an [indication]-less
 * clickable (see [bounceClick]) to replace the default ripple with the scale.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f,
): Modifier = composed {
    // Respect the user's motion preference — no scale feedback on LOW.
    if (LocalAnimationIntensity.current == AnimationIntensity.LOW) return@composed this
    val scale = remember { Animatable(1f) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> scale.animateTo(pressedScale, AntarMotion.fastSpatial())
                is PressInteraction.Release,
                is PressInteraction.Cancel -> scale.animateTo(1f, AntarMotion.spatial())
            }
        }
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * Convenience: a clickable surface with spring press feedback and no ripple.
 */
fun Modifier.bounceClick(
    pressedScale: Float = 0.96f,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this
        .pressScale(interactionSource, pressedScale)
        .clickable(interactionSource = interactionSource, indication = null) { onClick() }
}
