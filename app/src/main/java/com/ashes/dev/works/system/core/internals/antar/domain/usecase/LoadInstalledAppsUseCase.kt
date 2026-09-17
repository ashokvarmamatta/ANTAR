package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Stale-while-revalidate: emits the cached list at once (if there is one), then scans the device
 * and emits the fresh list, refreshing the cache. A failed scan only surfaces when nothing was
 * cached, so a PackageManager hiccup never replaces a good list with an error.
 */
class LoadInstalledAppsUseCase(private val repository: AppsRepository) {
    operator fun invoke(): Flow<AppResult<List<AppDetail>>> = flow {
        val cached = repository.getCachedApps()
        if (cached.isNotEmpty()) emit(AppResult.Success(cached))
        when (val fresh = repository.getInstalledApps()) {
            is AppResult.Success -> {
                repository.cacheApps(fresh.data)
                emit(fresh)
            }
            is AppResult.Failure -> if (cached.isEmpty()) emit(fresh)
        }
    }
}
