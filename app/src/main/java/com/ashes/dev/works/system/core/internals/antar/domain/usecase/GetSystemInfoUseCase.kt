package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.SystemInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository

class GetSystemInfoUseCase(private val repository: SystemRepository) {
    suspend operator fun invoke(): AppResult<SystemInfo> = repository.getSystemInfo()
}
