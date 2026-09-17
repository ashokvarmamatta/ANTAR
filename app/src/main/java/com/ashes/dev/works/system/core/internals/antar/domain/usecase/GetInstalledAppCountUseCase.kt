package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository

class GetInstalledAppCountUseCase(private val repository: AppsRepository) {
    suspend operator fun invoke(): AppResult<Int> = repository.getInstalledAppCount()
}
