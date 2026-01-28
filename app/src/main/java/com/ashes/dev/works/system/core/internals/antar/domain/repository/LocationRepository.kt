package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocation(): Flow<Location>
}
