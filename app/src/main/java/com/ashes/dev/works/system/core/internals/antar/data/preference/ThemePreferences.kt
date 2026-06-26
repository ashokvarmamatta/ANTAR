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
        private const val KEY_ANIMATION_INTENSITY = "animation_intensity"
        private const val KEY_ACCENT_INDEX = "accent_index"
        private const val KEY_APPS_CONSENT = "apps_consent_given"
        private const val KEY_APPS_CACHE = "apps_cache"

        const val MODE_SYSTEM = "system"
        const val MODE_LIGHT = "light"
        const val MODE_DARK = "dark"

        const val ANIM_LOW = "low"
        const val ANIM_MEDIUM = "medium"
        const val ANIM_HIGH = "high"
    }

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorsEnabled = MutableStateFlow(prefs.getBoolean(KEY_DYNAMIC_COLORS, false))
    val dynamicColorsEnabled: StateFlow<Boolean> = _dynamicColorsEnabled.asStateFlow()

    private val _animationIntensity = MutableStateFlow(prefs.getString(KEY_ANIMATION_INTENSITY, ANIM_HIGH) ?: ANIM_HIGH)
    val animationIntensity: StateFlow<String> = _animationIntensity.asStateFlow()

    private val _accentColorIndex = MutableStateFlow(prefs.getInt(KEY_ACCENT_INDEX, 0))
    val accentColorIndex: StateFlow<Int> = _accentColorIndex.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_THEME_MODE -> {
                _themeMode.value = prefs.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK
            }
            KEY_DYNAMIC_COLORS -> {
                _dynamicColorsEnabled.value = prefs.getBoolean(KEY_DYNAMIC_COLORS, false)
            }
            KEY_ANIMATION_INTENSITY -> {
                _animationIntensity.value = prefs.getString(KEY_ANIMATION_INTENSITY, ANIM_HIGH) ?: ANIM_HIGH
            }
            KEY_ACCENT_INDEX -> {
                _accentColorIndex.value = prefs.getInt(KEY_ACCENT_INDEX, 0)
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

    var animationIntensityStr: String
        get() = prefs.getString(KEY_ANIMATION_INTENSITY, ANIM_HIGH) ?: ANIM_HIGH
        set(value) = prefs.edit().putString(KEY_ANIMATION_INTENSITY, value).apply()

    var accentColorIndexValue: Int
        get() = prefs.getInt(KEY_ACCENT_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_ACCENT_INDEX, value).apply()

    // Apps screen: one-time consent + a lightweight cache of the scanned app list.
    var appsConsentGiven: Boolean
        get() = prefs.getBoolean(KEY_APPS_CONSENT, false)
        set(value) = prefs.edit().putBoolean(KEY_APPS_CONSENT, value).apply()

    var cachedAppsRaw: String
        get() = prefs.getString(KEY_APPS_CACHE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_APPS_CACHE, value).apply()
}
