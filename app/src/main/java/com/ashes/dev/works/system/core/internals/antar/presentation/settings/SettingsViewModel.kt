package com.ashes.dev.works.system.core.internals.antar.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveSettingsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetAccentColorUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetDynamicColorsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetMotionLevelUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    observeSettings: ObserveSettingsUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setDynamicColors: SetDynamicColorsUseCase,
    private val setAccentColor: SetAccentColorUseCase,
    private val setMotionLevel: SetMotionLevelUseCase
) : ViewModel() {

    private val applied = observeSettings().map { settings ->
        AppearanceChoice(settings.themeMode, settings.dynamicColors, settings.accentIndex, settings.motionLevel)
    }

    /** Null until the user changes something; then it holds the pending choice. */
    private val draft = MutableStateFlow<AppearanceChoice?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(applied, draft) { applied, draft ->
        SettingsUiState.Content(applied = applied, draft = draft ?: applied)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState.Loading)

    fun selectThemeMode(mode: ThemeMode) = editDraft { it.copy(themeMode = mode) }

    fun selectDynamicColors(enabled: Boolean) = editDraft { it.copy(dynamicColors = enabled) }

    fun selectAccent(index: Int) = editDraft { it.copy(accentIndex = index) }

    fun selectMotionLevel(level: MotionLevel) = editDraft { it.copy(motionLevel = level) }

    fun apply() {
        val state = uiState.value as? SettingsUiState.Content ?: return
        if (!state.hasChanges) return
        val choice = state.draft
        viewModelScope.launch {
            setThemeMode(choice.themeMode)
            setDynamicColors(choice.dynamicColors)
            setAccentColor(choice.accentIndex)
            setMotionLevel(choice.motionLevel)
            draft.value = null
        }
    }

    private fun editDraft(change: (AppearanceChoice) -> AppearanceChoice) {
        val state = uiState.value as? SettingsUiState.Content ?: return
        draft.value = change(state.draft)
    }
}
