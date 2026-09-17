package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository

class GetDeviceInfoUseCase(private val repository: DeviceRepository) {
    suspend operator fun invoke(): AppResult<DeviceInfo> = repository.getDeviceInfo()
}
