package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class GetCameraInfoUseCase(private val repository: CameraRepository) {
    suspend operator fun invoke(id: String): AppResult<CameraInfo> = repository.getCameraInfo(id)
}
