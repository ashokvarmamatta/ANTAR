package com.ashes.dev.works.system.core.internals.antar.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppSettings
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

/** The SharedPreferences file every release before 1.6 wrote settings to. */
internal const val LEGACY_PREFS_NAME = "antar_theme_prefs"
internal const val LEGACY_APPS_CACHE_KEY = "apps_cache"

private object Keys {
    val themeMode = stringPreferencesKey("theme_mode")
    val dynamicColors = booleanPreferencesKey("dynamic_colors")
    val introSeen = booleanPreferencesKey("intro_seen")
    val motionLevel = stringPreferencesKey("animation_intensity")
    val accentIndex = intPreferencesKey("accent_index")
    val appsConsent = booleanPreferencesKey("apps_consent_given")
}

fun createSettingsDataStore(context: Context, io: CoroutineDispatcher): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        // Same key names as the old SharedPreferences, so values carry over untouched. The apps
        // cache is not a setting and is moved to its own file by AppsCacheDataSource.
        migrations = listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = LEGACY_PREFS_NAME,
                keysToMigrate = setOf("theme_mode", "dynamic_colors", "intro_seen", "animation_intensity", "accent_index", "apps_consent_given")
            )
        ),
        scope = CoroutineScope(io + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("antar_settings") }
    )

class SettingsDataStoreRepository(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            AppSettings(
                themeMode = when (prefs[Keys.themeMode]) {
                    "system" -> ThemeMode.SYSTEM
                    "light" -> ThemeMode.LIGHT
                    else -> ThemeMode.DARK
                },
                dynamicColors = prefs[Keys.dynamicColors] ?: false,
                accentIndex = prefs[Keys.accentIndex] ?: 0,
                motionLevel = when (prefs[Keys.motionLevel]) {
                    "low" -> MotionLevel.LOW
                    "medium" -> MotionLevel.MEDIUM
                    else -> MotionLevel.HIGH
                },
                introSeen = prefs[Keys.introSeen] ?: false,
                appsConsentGiven = prefs[Keys.appsConsent] ?: false
            )
        }
        .distinctUntilChanged()

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.themeMode] = mode.name.lowercase() }
    }

    override suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { it[Keys.dynamicColors] = enabled }
    }

    override suspend fun setAccentIndex(index: Int) {
        dataStore.edit { it[Keys.accentIndex] = index }
    }

    override suspend fun setMotionLevel(level: MotionLevel) {
        dataStore.edit { it[Keys.motionLevel] = level.name.lowercase() }
    }

    override suspend fun setIntroSeen() {
        dataStore.edit { it[Keys.introSeen] = true }
    }

    override suspend fun setAppsConsentGiven() {
        dataStore.edit { it[Keys.appsConsent] = true }
    }
}
