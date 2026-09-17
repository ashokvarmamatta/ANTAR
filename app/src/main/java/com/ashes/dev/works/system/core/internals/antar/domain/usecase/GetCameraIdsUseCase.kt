package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class GetCameraIdsUseCase(private val repository: CameraRepository) {
    suspend operator fun invoke(): AppResult<List<String>> = repository.getCameraIds()
}
