package com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme

import android.provider.Settings
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/**
 * ANTAR motion system.
 *
 * A single, physics-based spring language shared across the app so every transition, press and
 * resize feels consistent and natural (overshoot + settle) instead of a fixed-duration timer.
 * Spatial springs drive size/offset/shape; effect springs drive color/alpha. Screens only call the
 * helpers in this file — no ad-hoc durations.
 */
object AntarMotion {

    /** Durations for the few tween-based effects (fades, entries, shimmer). */
    const val FAST_MS = 150
    const val MEDIUM_MS = 300
    const val ENTRY_STEP_MS = 20
    const val SHIMMER_MS = 1200

    /** Entrance of a hero element (splash mark, logo). */
    const val ENTRANCE_MS = 900

    /** Ambient loops behind illustrations and splash: slow, subtle, never essential. */
    const val AMBIENT_QUICK_MS = 900
    const val AMBIENT_FAST_MS = 1200
    const val AMBIENT_MEDIUM_MS = 1600
    const val AMBIENT_SLOW_MS = 2400
    const val AMBIENT_SWEEP_MS = 3600

    /** Only the first rows of a list stagger in; later rows appear as they scroll in. */
    const val STAGGER_CAP = 10

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
 * True when the system "Remove animations" / animator duration scale 0 setting is on. Compose does
 * not honour it on its own, so the app forces [AnimationIntensity.LOW] while it is set.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/**
 * A decorative infinite animation that holds still at [targetValue] on [AnimationIntensity.LOW]
 * (the low-motion setting and the system "remove animations" setting both map to LOW).
 */
@Composable
fun InfiniteTransition.ambientFloat(
    initialValue: Float,
    targetValue: Float,
    animationSpec: InfiniteRepeatableSpec<Float>,
    label: String
): State<Float> =
    if (LocalAnimationIntensity.current == AnimationIntensity.LOW) {
        remember(targetValue) { mutableFloatStateOf(targetValue) }
    } else {
        animateFloat(initialValue, targetValue, animationSpec, label)
    }

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

/** Cross-fade used by every loading → content → error swap. */
fun <S> AnimationIntensity.contentSwap(): AnimatedContentTransitionScope<S>.() -> ContentTransform = {
    if (this@contentSwap == AnimationIntensity.LOW) {
        EnterTransition.None togetherWith ExitTransition.None
    } else {
        fadeIn(tween(AntarMotion.MEDIUM_MS)) togetherWith fadeOut(tween(AntarMotion.FAST_MS))
    }
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

/**
 * Staggered entry for list/grid items: fade + slight scale-up, delayed by position. Capped at
 * [AntarMotion.STAGGER_CAP] so row 200 never waits, and read in the draw phase so it costs no
 * recomposition. No-op on [AnimationIntensity.LOW].
 */
fun Modifier.staggeredEntry(index: Int): Modifier = composed {
    if (LocalAnimationIntensity.current == AnimationIntensity.LOW || index >= AntarMotion.STAGGER_CAP) {
        return@composed this
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * AntarMotion.ENTRY_STEP_MS.toLong())
        progress.animateTo(1f, tween(AntarMotion.MEDIUM_MS))
    }
    graphicsLayer {
        alpha = progress.value
        val scale = 0.92f + 0.08f * progress.value
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Skeleton shimmer for content-shaped loading placeholders. A static tint on
 * [AnimationIntensity.LOW].
 */
fun Modifier.shimmer(): Modifier = composed {
    val base = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    val highlight = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    if (LocalAnimationIntensity.current == AnimationIntensity.LOW) {
        return@composed background(base)
    }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AntarMotion.SHIMMER_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerShift"
    )
    drawWithCache {
        onDrawBehind {
            val width = size.width
            val x = -width + 2 * width * shift
            drawRect(
                Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(x, 0f),
                    end = Offset(x + width, size.height)
                )
            )
        }
    }
}
