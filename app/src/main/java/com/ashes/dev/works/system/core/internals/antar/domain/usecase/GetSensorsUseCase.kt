package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository

class GetSensorsUseCase(private val repository: SensorsRepository) {
    suspend operator fun invoke(): AppResult<List<SensorInfo>> = repository.getSensors()
}
