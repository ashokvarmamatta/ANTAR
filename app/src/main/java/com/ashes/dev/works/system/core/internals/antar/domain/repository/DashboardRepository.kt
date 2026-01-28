package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardInfo(): Flow<Dashboard>
}
