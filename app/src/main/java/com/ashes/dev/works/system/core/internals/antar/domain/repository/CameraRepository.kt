package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera
import kotlinx.coroutines.flow.Flow

interface CameraRepository {
    fun getCamera(id: String): Camera
    fun getCameraIds(): List<String>
}
