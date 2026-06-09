package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.data.preference.ThemePreferences

class ThemeViewModel(private val themePreferences: ThemePreferences) : ViewModel() {

    val themeMode = themePreferences.themeMode
    val dynamicColorsEnabled = themePreferences.dynamicColorsEnabled

    fun setThemeMode(mode: String) {
        themePreferences.themeModeStr = mode
    }

    fun setDynamicColorsEnabled(enabled: Boolean) {
        themePreferences.dynamicColorsEnabledBool = enabled
    }
}
