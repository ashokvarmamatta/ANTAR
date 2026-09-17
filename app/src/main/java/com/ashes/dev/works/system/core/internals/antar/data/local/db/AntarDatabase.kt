package com.ashes.dev.works.system.core.internals.antar.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BatteryLog::class], version = 1, exportSchema = true)
abstract class AntarDatabase : RoomDatabase() {
    abstract fun batteryLogDao(): BatteryLogDao
}
