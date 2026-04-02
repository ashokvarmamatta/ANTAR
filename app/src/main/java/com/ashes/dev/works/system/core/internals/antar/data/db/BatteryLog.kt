package com.ashes.dev.works.system.core.internals.antar.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_log")
data class BatteryLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val batteryLevel: Int,
    val isCharging: Boolean,
    val temperature: Int,       // in 0.1°C
    val current: Int,           // in uA
    val power: Double,          // in W
    val voltage: Double,        // in V
    val remainingCapacity: Int  // in mAh
)
