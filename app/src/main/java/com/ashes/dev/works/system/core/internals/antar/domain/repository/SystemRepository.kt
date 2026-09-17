package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.SystemInfo
import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    suspend fun getSystemInfo(): AppResult<SystemInfo>

    /** Time since boot in milliseconds, emitted once per second while collected. */
    fun observeUptimeMillis(): Flow<Long>
}
