package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.data.local.db.BatteryLogDao
import com.ashes.dev.works.system.core.internals.antar.data.mapper.toBatteryLog
import com.ashes.dev.works.system.core.internals.antar.data.mapper.toDomain
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryHealth
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryRecord
import com.ashes.dev.works.system.core.internals.antar.domain.model.ChargerType
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class BatteryRepositoryImpl(
    private val context: Context,
    private val dao: BatteryLogDao,
    private val io: CoroutineDispatcher
) : BatteryRepository {

    /** Values that stay fixed for the life of one collection. */
    private data class StaticInfo(
        val designCapacityMah: Int?,
        val estimatedMaxCapacityMah: Int?,
        val chargeCycles: Int?
    )

    override fun getBatteryInfo(): Flow<Battery> = callbackFlow {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // These are effectively static for the life of a collection (design capacity never changes;
        // estimated max capacity and cycle count drift only very slowly). Reflection / sysfs reads
        // are expensive, so resolve them ONCE here instead of on every poll tick.
        val staticInfo = StaticInfo(
            designCapacityMah = getDesignCapacityMah(),
            estimatedMaxCapacityMah = estimateMaxCapacityMah(batteryManager),
            chargeCycles = getBatteryCycleCount()
        )

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(readBattery(intent, batteryManager, staticInfo))
            }
        }

        fun pollBattery() {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (intent != null) {
                trySend(readBattery(intent, batteryManager, staticInfo))
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // The receiver already pushes an update on every real battery change. We poll only to refresh
        // live current/power/temperature; 2s keeps it responsive while cutting per-tick work ~4x.
        val pollJob = launch {
            while (true) {
                pollBattery()
                delay(2000)
            }
        }

        awaitClose {
            context.unregisterReceiver(receiver)
            pollJob.cancel()
        }
    }.distinctUntilChanged() // Only emit when the Battery data actually changes
        .flowOn(io) // PowerProfile reflection, sticky-intent and sysfs reads stay off main

    override fun getBatteryHistory(sinceMillis: Long): Flow<List<BatteryRecord>> =
        dao.getLogsSince(sinceMillis)
            .map { logs -> logs.toDomain() }
            .flowOn(io)

    override suspend fun logCurrentBattery(): Boolean = withContext(io) {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return@withContext false
        // Capacity/cycle facts are not stored, so skip the expensive reflection and sysfs reads.
        val reading = readBattery(intent, batteryManager, StaticInfo(null, null, null))
        dao.insert(reading.toBatteryLog(System.currentTimeMillis()))
        true
    }

    override suspend fun deleteLogsOlderThan(beforeMillis: Long): Unit = withContext(io) {
        dao.deleteOlderThan(beforeMillis)
    }

    private fun readBattery(intent: Intent, batteryManager: BatteryManager, staticInfo: StaticInfo): Battery {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val levelPercent = if (scale > 0 && level >= 0) level * 100 / scale else 0

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).takeIf { it > 0 }
        val voltageVolts = voltageMv?.let { it / 1000.0 }
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
            .takeIf { it != Int.MIN_VALUE }

        val remainingMah = batteryManager.readProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            ?.takeIf { it > 0 }
            ?.let { it / 1000 }
        val currentUa = batteryManager.readProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val powerWatts = if (currentUa != null && voltageVolts != null) {
            (currentUa / 1_000_000.0) * voltageVolts
        } else {
            null
        }

        val estimatedMax = staticInfo.estimatedMaxCapacityMah
        val preciseLevel = if (estimatedMax != null && remainingMah != null) {
            remainingMah.toDouble() / estimatedMax.toDouble() * 100.0
        } else {
            levelPercent.toDouble()
        }.coerceIn(0.0, 100.0)

        val design = staticInfo.designCapacityMah
        val capacityHealthPercent = if (design != null && estimatedMax != null) {
            (estimatedMax.toDouble() / design * 100).toInt().coerceAtMost(100)
        } else {
            null
        }

        return Battery(
            levelPercent = levelPercent,
            preciseLevelPercent = preciseLevel,
            isCharging = isCharging,
            currentMicroAmps = currentUa,
            powerWatts = powerWatts,
            temperatureDeciCelsius = temperature,
            voltageVolts = voltageVolts,
            health = healthOf(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)),
            chargerType = chargerTypeOf(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)?.takeIf { it.isNotBlank() },
            designCapacityMah = design,
            estimatedMaxCapacityMah = estimatedMax,
            remainingCapacityMah = remainingMah,
            chargeCycles = staticInfo.chargeCycles,
            capacityHealthPercent = capacityHealthPercent
        )
    }

    /** BatteryManager returns Int.MIN_VALUE for a property the device does not support. */
    private fun BatteryManager.readProperty(id: Int): Int? =
        getIntProperty(id).takeIf { it != Int.MIN_VALUE }

    private fun healthOf(health: Int): BatteryHealth = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
        BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
        BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
        else -> BatteryHealth.UNKNOWN
    }

    private fun chargerTypeOf(plugged: Int): ChargerType = when (plugged) {
        BatteryManager.BATTERY_PLUGGED_AC -> ChargerType.AC
        BatteryManager.BATTERY_PLUGGED_USB -> ChargerType.USB
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> ChargerType.WIRELESS
        PLUGGED_DOCK -> ChargerType.DOCK
        else -> ChargerType.NONE
    }

    private fun getDesignCapacityMah(): Int? = try {
        val powerProfile = Class.forName(POWER_PROFILE_CLASS)
            .getConstructor(Context::class.java)
            .newInstance(context)
        val capacity = Class.forName(POWER_PROFILE_CLASS)
            .getMethod("getAveragePower", String::class.java)
            .invoke(powerProfile, "battery.capacity") as Double
        capacity.toInt().takeIf { it > 0 }
    } catch (e: Exception) {
        // Hidden API: absent or blocked on this device.
        null
    }

    private fun estimateMaxCapacityMah(batteryManager: BatteryManager): Int? {
        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val levelFraction = if (scale > 0) level.toFloat() / scale else 0f

        if (chargeCounter > 0 && levelFraction > 0) {
            return ((chargeCounter / levelFraction) / 1000).toInt().takeIf { it > 0 }
        }
        return null
    }

    private fun getBatteryCycleCount(): Int? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            return batteryStatus?.getIntExtra(EXTRA_CYCLE_COUNT, -1)?.takeIf { it > 0 }
        }

        for (path in CYCLE_COUNT_PATHS) {
            try {
                val file = File(path)
                if (file.exists() && file.canRead()) {
                    val value = file.readText().trim().toIntOrNull()
                    if (value != null && value > 0) return value
                }
            } catch (e: IOException) {
                continue
            } catch (e: SecurityException) {
                continue
            }
        }
        return null
    }

    private companion object {
        const val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"
        const val EXTRA_CYCLE_COUNT = "android.os.extra.CYCLE_COUNT"

        /** `BatteryManager.BATTERY_PLUGGED_DOCK` (API 33); the value is stable, so older SDKs can compare it. */
        const val PLUGGED_DOCK = 8

        val CYCLE_COUNT_PATHS = listOf(
            "/sys/class/power_supply/battery/cycle_count",
            "/sys/class/power_supply/bms/cycle_count"
        )
    }
}
