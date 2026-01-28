package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera

interface CameraRepository {
    fun getCamera(): Camera
}
