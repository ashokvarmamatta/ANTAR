package com.ashes.dev.works.system.core.internals.antar.presentation.common

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.AppError

/** The one place an [AppError] becomes user-facing copy. */
@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.PermissionDenied -> R.string.common_error_permission
    AppError.Unavailable -> R.string.common_error_unavailable
    is AppError.Unexpected -> R.string.common_error_generic
}
