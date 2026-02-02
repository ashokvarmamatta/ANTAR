package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class CameraViewModel(private val cameraRepository: CameraRepository) : ViewModel() {
    val cameraIds = cameraRepository.getCameraIds()
    var selectedCameraId by mutableStateOf(cameraIds.firstOrNull() ?: "")
        private set

    fun getCamera() = cameraRepository.getCamera(selectedCameraId)

    fun selectCamera(id: String) {
        selectedCameraId = id
    }
}
