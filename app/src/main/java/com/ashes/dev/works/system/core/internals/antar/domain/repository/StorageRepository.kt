package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo

interface StorageRepository {
    suspend fun getStorageInfo(): AppResult<StorageInfo>
}
