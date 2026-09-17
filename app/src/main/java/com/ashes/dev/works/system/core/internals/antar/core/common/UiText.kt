package com.ashes.dev.works.system.core.internals.antar.core.common

import androidx.annotation.StringRes

/**
 * Text that reaches the screen. State and ViewModels carry this instead of a `String`, so every
 * user-facing word is resolved from `strings.xml` at the UI edge; [Raw] is only for values read
 * from the device (a model name, an IP address), never for prose.
 */
sealed interface UiText {
    data class Res(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
    data class Raw(val value: String) : UiText
}
