package com.ashes.dev.works.system.core.internals.antar.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryLogDao {

    @Insert
    suspend fun insert(log: BatteryLog)

    @Query("SELECT * FROM battery_log WHERE timestamp >= :since ORDER BY timestamp ASC")
    fun getLogsSince(since: Long): Flow<List<BatteryLog>>

    @Query("SELECT * FROM battery_log ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLog(): BatteryLog?

    @Query("DELETE FROM battery_log WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM battery_log")
    suspend fun count(): Int
}
