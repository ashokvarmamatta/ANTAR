package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryRecord
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    /** Live readings: pushed on every battery broadcast and refreshed by a short poll while collected. */
    fun getBatteryInfo(): Flow<Battery>

    /** Stored readings at or after [sinceMillis], oldest first. */
    fun getBatteryHistory(sinceMillis: Long): Flow<List<BatteryRecord>>

    /** Stores one reading now. Returns false when the platform had no battery state to read. */
    suspend fun logCurrentBattery(): Boolean

    /** Deletes stored readings older than [beforeMillis]. */
    suspend fun deleteLogsOlderThan(beforeMillis: Long)
}
