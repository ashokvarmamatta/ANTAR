package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository

class GetCpuInfoUseCase(private val repository: CpuRepository) {
    suspend operator fun invoke(): AppResult<CpuInfo> = repository.getCpuInfo()
}
