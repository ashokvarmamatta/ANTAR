package com.ashes.dev.works.system.core.internals.antar.presentation.camera

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCameraIdsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCameraInfoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getCameraIds: GetCameraIdsUseCase,
    private val getCameraInfo: GetCameraInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Loading)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    /** Reads the camera list, then every camera's characteristics, the selected one first. */
    fun load() {
        loadJob?.cancel()
        _uiState.value = CameraUiState.Loading
        loadJob = viewModelScope.launch {
            when (val result = getCameraIds()) {
                is AppResult.Failure -> {
                    _uiState.value = CameraUiState.Error(result.error.messageRes())
                }
                is AppResult.Success -> {
                    val ids = result.data
                    if (ids.isEmpty()) {
                        _uiState.value = CameraUiState.Empty
                        return@launch
                    }
                    val selected = savedStateHandle.get<String>(KEY_SELECTED_ID)?.takeIf { it in ids } ?: ids.first()
                    savedStateHandle[KEY_SELECTED_ID] = selected
                    _uiState.value = CameraUiState.Content(cameraIds = ids, selectedId = selected)

                    loadInfo(selected)
                    ids.filter { it != selected }.forEach { loadInfo(it) }
                }
            }
        }
    }

    fun selectCamera(id: String) {
        savedStateHandle[KEY_SELECTED_ID] = id
        _uiState.update { state ->
            if (state is CameraUiState.Content && id in state.cameraIds) state.copy(selectedId = id) else state
        }
    }

    /** Retries the selected camera after its characteristics failed to load. */
    fun retrySelected() {
        val state = _uiState.value as? CameraUiState.Content ?: return
        val id = state.selectedId
        _uiState.update { current ->
            if (current is CameraUiState.Content) current.copy(infoErrors = current.infoErrors - id) else current
        }
        viewModelScope.launch { loadInfo(id) }
    }

    private suspend fun loadInfo(id: String) {
        val result = getCameraInfo(id)
        _uiState.update { state ->
            if (state !is CameraUiState.Content) return@update state
            when (result) {
                is AppResult.Success -> state.copy(
                    infos = state.infos + (id to result.data),
                    infoErrors = state.infoErrors - id
                )
                is AppResult.Failure -> state.copy(infoErrors = state.infoErrors + (id to result.error.messageRes()))
            }
        }
    }

    private companion object {
        const val KEY_SELECTED_ID = "camera_selected_id"
    }
}
