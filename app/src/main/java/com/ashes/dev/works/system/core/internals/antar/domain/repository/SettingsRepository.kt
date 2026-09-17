package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.AppSettings
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setAccentIndex(index: Int)
    suspend fun setMotionLevel(level: MotionLevel)
    suspend fun setIntroSeen()
    suspend fun setAppsConsentGiven()
}
