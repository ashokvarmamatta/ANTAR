package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.LensFacing
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelSize

// ── Formatting ───────────────────────────────────────────────────────

@Composable
internal fun sizeText(size: PixelSize): String = stringResource(R.string.camera_value_size, size.width, size.height)

/** Null when not reported, "None" when reported empty, otherwise the raw Camera2 ids joined. */
@Composable
internal fun modesText(modes: List<Int>?, separator: String): String? = when {
    modes == null -> null
    modes.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> modes.joinToString(separator)
}

@Composable
internal fun sizesText(sizes: List<PixelSize>?, separator: String): String? = when {
    sizes == null -> null
    sizes.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> sizes.map { sizeText(it) }.joinToString(separator)
}

// ── Enum → string resource ───────────────────────────────────────────

@StringRes
internal fun facingRes(facing: LensFacing): Int = when (facing) {
    LensFacing.FRONT -> R.string.camera_facing_front
    LensFacing.BACK -> R.string.camera_facing_back
    LensFacing.EXTERNAL -> R.string.camera_facing_external
}
