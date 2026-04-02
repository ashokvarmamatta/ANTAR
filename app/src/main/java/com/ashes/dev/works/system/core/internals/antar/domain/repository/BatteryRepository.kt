package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import kotlinx.coroutines.flow.Flow

interface BatteryRepository {
    fun getBatteryInfo(): Flow<Battery>
    fun getBatteryHistory(sinceMillis: Long): Flow<List<BatteryLog>>
    suspend fun logCurrentBattery()
}
