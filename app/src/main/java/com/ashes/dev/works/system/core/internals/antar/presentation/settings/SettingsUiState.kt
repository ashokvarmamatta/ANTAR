package com.ashes.dev.works.system.core.internals.antar.presentation.settings

import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode

/** The appearance choices the Settings screen edits. */
data class AppearanceChoice(
    val themeMode: ThemeMode,
    val dynamicColors: Boolean,
    val accentIndex: Int,
    val motionLevel: MotionLevel
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    /**
     * [applied] is what the app is running with; [draft] is what the user has ticked. Nothing
     * changes until Apply commits the draft, and the screen stays put and redraws.
     */
    data class Content(
        val applied: AppearanceChoice,
        val draft: AppearanceChoice
    ) : SettingsUiState {
        val hasChanges: Boolean get() = draft != applied
    }
}
