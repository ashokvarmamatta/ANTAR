package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.data.preference.ThemePreferences
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(private val themePreferences: ThemePreferences) : ViewModel() {

    val themeMode: StateFlow<String> = themePreferences.themeMode
    val dynamicColorsEnabled: StateFlow<Boolean> = themePreferences.dynamicColorsEnabled
    val animationIntensity: StateFlow<String> = themePreferences.animationIntensity
    val accentColorIndex: StateFlow<Int> = themePreferences.accentColorIndex

    fun setThemeMode(mode: String) {
        themePreferences.themeModeStr = mode
    }

    fun setDynamicColorsEnabled(enabled: Boolean) {
        themePreferences.dynamicColorsEnabledBool = enabled
    }

    fun setAnimationIntensity(value: String) {
        themePreferences.animationIntensityStr = value
    }

    fun setAccentColorIndex(index: Int) {
        themePreferences.accentColorIndexValue = index
    }
}
