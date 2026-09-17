package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo

interface SensorsRepository {
    /** Every sensor on the device, sorted by name. */
    suspend fun getSensors(): AppResult<List<SensorInfo>>
}
