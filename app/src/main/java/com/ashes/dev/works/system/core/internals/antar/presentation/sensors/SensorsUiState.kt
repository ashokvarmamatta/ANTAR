package com.ashes.dev.works.system.core.internals.antar.presentation.sensors

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo

sealed interface SensorsUiState {
    data object Loading : SensorsUiState
    data class Content(val sensors: List<SensorInfo>) : SensorsUiState
    data object Empty : SensorsUiState
    data class Error(@param:StringRes val message: Int) : SensorsUiState
}
