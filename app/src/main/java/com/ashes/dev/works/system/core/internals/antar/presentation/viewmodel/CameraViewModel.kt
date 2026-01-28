package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class CameraViewModel(private val cameraRepository: CameraRepository) : ViewModel() {
    fun getCamera() = cameraRepository.getCamera()
}
