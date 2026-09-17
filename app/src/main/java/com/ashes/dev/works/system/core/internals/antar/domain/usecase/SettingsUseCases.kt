package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.domain.model.AppSettings
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}

class SetThemeModeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}

class SetDynamicColorsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDynamicColors(enabled)
}

class SetAccentColorUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(index: Int) = repository.setAccentIndex(index)
}

class SetMotionLevelUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(level: MotionLevel) = repository.setMotionLevel(level)
}

class CompleteIntroUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.setIntroSeen()
}

class GiveAppsConsentUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.setAppsConsentGiven()
}
