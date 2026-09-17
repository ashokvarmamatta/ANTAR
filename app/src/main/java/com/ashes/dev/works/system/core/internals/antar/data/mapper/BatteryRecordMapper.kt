package com.ashes.dev.works.system.core.internals.antar.data.mapper

import com.ashes.dev.works.system.core.internals.antar.data.local.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryRecord

/** Placeholder stored for an integer the device did not report (matches rows written before nulls). */
private const val UNKNOWN_INT = -1

fun BatteryLog.toDomain(): BatteryRecord = BatteryRecord(
    timestampMillis = timestamp,
    levelPercent = batteryLevel,
    isCharging = isCharging,
    temperatureDeciCelsius = temperature,
    currentMicroAmps = current,
    powerWatts = power,
    voltageVolts = voltage,
    remainingCapacityMah = remainingCapacity
)

fun List<BatteryLog>.toDomain(): List<BatteryRecord> = map { it.toDomain() }

/** The row stored by the background log for a live reading taken at [timestampMillis]. */
fun Battery.toBatteryLog(timestampMillis: Long): BatteryLog = BatteryLog(
    timestamp = timestampMillis,
    batteryLevel = levelPercent,
    isCharging = isCharging,
    temperature = temperatureDeciCelsius ?: UNKNOWN_INT,
    current = currentMicroAmps ?: 0,
    power = powerWatts ?: 0.0,
    voltage = voltageVolts ?: 0.0,
    remainingCapacity = remainingCapacityMah ?: UNKNOWN_INT
)
