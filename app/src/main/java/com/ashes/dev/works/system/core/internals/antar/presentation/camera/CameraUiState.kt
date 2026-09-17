package com.ashes.dev.works.system.core.internals.antar.presentation.camera

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo

sealed interface CameraUiState {
    data object Loading : CameraUiState

    /** The device reports no cameras at all. */
    data object Empty : CameraUiState

    /**
     * @param infos characteristics already read, by camera id (all cameras are read so every
     * selector card can show its megapixels and facing).
     * @param infoErrors cameras whose characteristics could not be read, by camera id.
     */
    data class Content(
        val cameraIds: List<String>,
        val selectedId: String,
        val infos: Map<String, CameraInfo> = emptyMap(),
        val infoErrors: Map<String, Int> = emptyMap()
    ) : CameraUiState {
        val selectedInfo: CameraInfo? get() = infos[selectedId]

        @get:StringRes
        val selectedError: Int? get() = infoErrors[selectedId]

        val isSelectedLoading: Boolean get() = selectedInfo == null && selectedError == null
    }

    data class Error(@param:StringRes val message: Int) : CameraUiState
}
