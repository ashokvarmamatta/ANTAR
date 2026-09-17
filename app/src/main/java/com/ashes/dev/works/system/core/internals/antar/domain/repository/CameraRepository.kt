package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo

interface CameraRepository {
    suspend fun getCameraIds(): AppResult<List<String>>
    suspend fun getCameraInfo(id: String): AppResult<CameraInfo>
}
