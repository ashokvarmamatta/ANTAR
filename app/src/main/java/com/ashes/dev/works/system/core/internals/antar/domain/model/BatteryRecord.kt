package com.ashes.dev.works.system.core.internals.antar.domain.model

/**
 * One stored battery reading from the background log. Values are exactly what was stored; a reading
 * the device did not report was stored as a negative / zero placeholder.
 */
data class BatteryRecord(
    val timestampMillis: Long,
    val levelPercent: Int,
    val isCharging: Boolean,
    val temperatureDeciCelsius: Int,
    val currentMicroAmps: Int,
    val powerWatts: Double,
    val voltageVolts: Double,
    val remainingCapacityMah: Int
)
