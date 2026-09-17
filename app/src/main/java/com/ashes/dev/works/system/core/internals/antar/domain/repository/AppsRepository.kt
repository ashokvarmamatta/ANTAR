package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail

interface AppsRepository {
    suspend fun getInstalledApps(): AppResult<List<AppDetail>>
    suspend fun getInstalledAppCount(): AppResult<Int>
    suspend fun getCachedApps(): List<AppDetail>
    suspend fun cacheApps(apps: List<AppDetail>)
}
