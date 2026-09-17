package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository

class GetStorageInfoUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(): AppResult<StorageInfo> = repository.getStorageInfo()
}
