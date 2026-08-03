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

    private val _appsConsentGiven = MutableStateFlow(prefs.getBoolean(KEY_APPS_CONSENT, false))
    val appsConsentGivenFlow: StateFlow<Boolean> = _appsConsentGiven.asStateFlow()

    private val _introSeen = MutableStateFlow(prefs.getBoolean(KEY_INTRO_SEEN, false))
    val introSeenFlow: StateFlow<Boolean> = _introSeen.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            KEY_THEME_MODE -> {
                _themeMode.value = sp.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK
            }
            KEY_DYNAMIC_COLORS -> {
                _dynamicColorsEnabled.value = sp.getBoolean(KEY_DYNAMIC_COLORS, false)
            }
            KEY_ANIMATION_INTENSITY -> {
                _animationIntensity.value = sp.getString(KEY_ANIMATION_INTENSITY, ANIM_HIGH) ?: ANIM_HIGH
            }
            KEY_ACCENT_INDEX -> {
                _accentColorIndex.value = sp.getInt(KEY_ACCENT_INDEX, 0)
            }
            KEY_APPS_CONSENT -> {
                _appsConsentGiven.value = sp.getBoolean(KEY_APPS_CONSENT, false)
            }
            KEY_INTRO_SEEN -> {
                _introSeen.value = sp.getBoolean(KEY_INTRO_SEEN, false)
            }
            null -> {
                // If key is null, all preferences were changed or cleared
                _themeMode.value = sp.getString(KEY_THEME_MODE, MODE_DARK) ?: MODE_DARK
                _dynamicColorsEnabled.value = sp.getBoolean(KEY_DYNAMIC_COLORS, false)
                _animationIntensity.value = sp.getString(KEY_ANIMATION_INTENSITY, ANIM_HIGH) ?: ANIM_HIGH
                _accentColorIndex.value = sp.getInt(KEY_ACCENT_INDEX, 0)
                _appsConsentGiven.value = sp.getBoolean(KEY_APPS_CONSENT, false)
                _introSeen.value = sp.getBoolean(KEY_INTRO_SEEN, false)
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    var themeModeStr: String
        get() = _themeMode.value
        set(value) {
            if (_themeMode.value != value) {
                prefs.edit().putString(KEY_THEME_MODE, value).apply()
                _themeMode.value = value
            }
        }

    var dynamicColorsEnabledBool: Boolean
        get() = _dynamicColorsEnabled.value
        set(value) {
            if (_dynamicColorsEnabled.value != value) {
                prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, value).apply()
                _dynamicColorsEnabled.value = value
            }
        }

    var introSeen: Boolean
        get() = _introSeen.value
        set(value) {
            if (_introSeen.value != value) {
                prefs.edit().putBoolean(KEY_INTRO_SEEN, value).apply()
                _introSeen.value = value
            }
        }

    var animationIntensityStr: String
        get() = _animationIntensity.value
        set(value) {
            if (_animationIntensity.value != value) {
                prefs.edit().putString(KEY_ANIMATION_INTENSITY, value).apply()
                _animationIntensity.value = value
            }
        }

    var accentColorIndexValue: Int
        get() = _accentColorIndex.value
        set(value) {
            if (_accentColorIndex.value != value) {
                prefs.edit().putInt(KEY_ACCENT_INDEX, value).apply()
                _accentColorIndex.value = value
            }
        }

    // Apps screen: one-time consent + a lightweight cache of the scanned app list.
    var appsConsentGiven: Boolean
        get() = _appsConsentGiven.value
        set(value) {
            if (_appsConsentGiven.value != value) {
                prefs.edit().putBoolean(KEY_APPS_CONSENT, value).apply()
                _appsConsentGiven.value = value
            }
        }

    var cachedAppsRaw: String
        get() = prefs.getString(KEY_APPS_CACHE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_APPS_CACHE, value).apply()
}
