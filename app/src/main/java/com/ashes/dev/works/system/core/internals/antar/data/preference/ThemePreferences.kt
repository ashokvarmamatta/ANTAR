package com.ashes.dev.works.system.core.internals.antar.data.preference

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("antar_theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLORS = "dynamic_colors"
        private const val KEY_INTRO_SEEN = "intro_seen"

        const val MODE_SYSTEM = "system"
        const val MODE_LIGHT = "light"
        const val MODE_DARK = "dark"
    }

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorsEnabled = MutableStateFlow(prefs.getBoolean(KEY_DYNAMIC_COLORS, false))
    val dynamicColorsEnabled: StateFlow<Boolean> = _dynamicColorsEnabled.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_THEME_MODE -> {
                _themeMode.value = prefs.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK
            }
            KEY_DYNAMIC_COLORS -> {
                _dynamicColorsEnabled.value = prefs.getBoolean(KEY_DYNAMIC_COLORS, false)
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    var themeModeStr: String
        get() = prefs.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var dynamicColorsEnabledBool: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLORS, false)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, value).apply()

    var introSeen: Boolean
        get() = prefs.getBoolean(KEY_INTRO_SEEN, false)
        set(value) = prefs.edit().putBoolean(KEY_INTRO_SEEN, value).apply()
}
