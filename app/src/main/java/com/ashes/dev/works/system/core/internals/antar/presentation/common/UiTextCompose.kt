package com.ashes.dev.works.system.core.internals.antar.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.core.common.UiText

/** Resolves [UiText] at the UI edge. */
@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Raw -> value
    is UiText.Res -> stringResource(id, *args.toTypedArray())
}
