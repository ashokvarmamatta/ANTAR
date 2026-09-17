package com.ashes.dev.works.system.core.internals.antar.domain.model

/** Health the platform reports for the cell (`BatteryManager.EXTRA_HEALTH`). */
enum class BatteryHealth { GOOD, OVERHEAT, DEAD, OVER_VOLTAGE, UNSPECIFIED_FAILURE, COLD, UNKNOWN }

/** What the device is plugged into (`BatteryManager.EXTRA_PLUGGED`). [NONE] = running on battery. */
enum class ChargerType { AC, USB, WIRELESS, DOCK, NONE }

/**
 * One live battery reading. Raw facts only: units are in the field names, null means the device did
 * not report the value. The UI formats and labels everything.
 */
data class Battery(
    /** Whole-percent level from the battery broadcast, 0..100. */
    val levelPercent: Int,
    /** Level derived from the charge counter and estimated max capacity; falls back to [levelPercent]. */
    val preciseLevelPercent: Double,
    val isCharging: Boolean,
    /** Instantaneous current; sign convention is device specific. */
    val currentMicroAmps: Int?,
    val powerWatts: Double?,
    /** Tenths of a degree Celsius, e.g. 312 = 31.2 °C. */
    val temperatureDeciCelsius: Int?,
    val voltageVolts: Double?,
    val health: BatteryHealth,
    val chargerType: ChargerType,
    /** Chemistry token reported by the platform, e.g. "Li-ion" (a technical id, not prose). */
    val technology: String?,
    val designCapacityMah: Int?,
    val estimatedMaxCapacityMah: Int?,
    val remainingCapacityMah: Int?,
    val chargeCycles: Int?,
    /** Estimated max capacity as a percentage of design capacity, capped at 100. */
    val capacityHealthPercent: Int?
)
