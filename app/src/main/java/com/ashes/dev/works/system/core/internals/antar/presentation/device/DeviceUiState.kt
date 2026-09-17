package com.ashes.dev.works.system.core.internals.antar.presentation.device

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceInfo

sealed interface DeviceUiState {
    data object Loading : DeviceUiState
    data class Content(val info: DeviceInfo) : DeviceUiState
    data class Error(@param:StringRes val message: Int) : DeviceUiState
}
