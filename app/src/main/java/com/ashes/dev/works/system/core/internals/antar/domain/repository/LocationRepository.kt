package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Live fixes while collected. Uses GNSS, satellites and DOP with precise location, and only the
     * network provider with approximate location. Fails with [SecurityException] without either.
     */
    fun observeLocation(): Flow<Location>

    fun observeGpsEnabled(): Flow<Boolean>
}
