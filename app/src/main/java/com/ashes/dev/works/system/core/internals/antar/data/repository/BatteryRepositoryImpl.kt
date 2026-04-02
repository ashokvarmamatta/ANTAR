package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLogDao
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class BatteryRepositoryImpl(
    private val context: Context,
    private val batteryLogDao: BatteryLogDao
) : BatteryRepository {

    override fun getBatteryInfo(): Flow<Battery> = callbackFlow {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                sendBatteryUpdate(intent, batteryManager)
            }
        }

        fun pollBattery() {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (intent != null) {
                sendBatteryUpdate(intent, batteryManager)
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Polling every 500ms to balance real-time feel and performance (prevents lagging)
        val pollJob = launch {
            while (true) {
                pollBattery()
                delay(500)
            }
        }

        awaitClose {
            context.unregisterReceiver(receiver)
            pollJob.cancel()
        }
    }.distinctUntilChanged() // Only emit when the Battery data actually changes

    override fun getBatteryHistory(sinceMillis: Long): Flow<List<BatteryLog>> {
        return batteryLogDao.getLogsSince(sinceMillis)
    }

    override suspend fun logCurrentBattery() {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (scale > 0) (level * 100 / scale) else 0

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        val currentNowUa = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val remainingCapacityUah = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val powerWatt = (currentNowUa / 1_000_000.0) * (voltage / 1000.0)

        val log = BatteryLog(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            temperature = temperature,
            current = currentNowUa,
            power = powerWatt,
            voltage = voltage / 1000.0,
            remainingCapacity = remainingCapacityUah / 1000
        )
        batteryLogDao.insert(log)
    }

    private fun kotlinx.coroutines.channels.ProducerScope<Battery>.sendBatteryUpdate(intent: Intent, batteryManager: BatteryManager) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (scale > 0) (level * 100 / scale) else 0

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "- - -"

        val remainingCapacityUah = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val currentNowUa = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val powerWatt = (currentNowUa / 1_000_000.0) * (voltage / 1000.0)

        val designCapacity = getDesignCapacity(context)
        val estimatedMaxCapacity = estimateMaxCapacity(context)
        val chargeCycles = getBatteryCycleCount(context)

        val preciseLevel = if (estimatedMaxCapacity > 0) {
            (remainingCapacityUah.toDouble() / 1000.0) / estimatedMaxCapacity.toDouble() * 100.0
        } else {
            batteryPct.toDouble()
        }

        val batteryHealthStatus = if (designCapacity != -1.0 && estimatedMaxCapacity != -1) {
            val percentage = (estimatedMaxCapacity.toDouble() / designCapacity * 100).toInt()
            when {
                percentage > 95 -> "Excellent ($percentage%)"
                percentage > 90 -> "Very Good ($percentage%)"
                percentage > 85 -> "Good ($percentage%)"
                else -> "Fair ($percentage%)"
            }
        } else {
            "- - -"
        }

        val battery = Battery(
            batteryLevel = batteryPct,
            preciseLevel = preciseLevel,
            isCharging = isCharging,
            current = currentNowUa,
            power = powerWatt,
            temperature = temperature,
            health = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                else -> "Unknown"
            },
            chargerType = getChargerType(intent),
            technology = technology,
            voltage = voltage / 1000.0,
            designCapacity = designCapacity.toInt(),
            estimatedMaxCapacity = estimatedMaxCapacity,
            remainingCapacity = remainingCapacityUah / 1000,
            chargeCycles = chargeCycles,
            batteryHealthStatus = batteryHealthStatus
        )
        trySend(battery)
    }

    private fun getChargerType(intent: Intent): String {
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger (Wall)"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port (Slow)"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Unknown / Battery"
        }
    }

    private fun getDesignCapacity(context: Context): Double {
        val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"

        try {
            val mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                .getConstructor(Context::class.java)
                .newInstance(context)

            return Class.forName(POWER_PROFILE_CLASS)
                .getMethod("getAveragePower", String::class.java)
                .invoke(mPowerProfile, "battery.capacity") as Double
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1.0
    }

    private fun estimateMaxCapacity(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val levelPercent = if (scale > 0) level.toFloat() / scale else 0f

        if (chargeCounter > 0 && levelPercent > 0) {
            return ((chargeCounter / levelPercent) / 1000).toInt()
        }
        return -1
    }

    private fun getBatteryCycleCount(context: Context): Int {
        if (Build.VERSION.SDK_INT >= 34) {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
            return batteryStatus?.getIntExtra("android.os.extra.CYCLE_COUNT", -1) ?: -1
        }

        val paths = listOf(
            "/sys/class/power_supply/battery/cycle_count",
            "/sys/class/power_supply/bms/cycle_count"
        )

        for (path in paths) {
            try {
                val file = File(path)
                if (file.exists() && file.canRead()) {
                    val value = file.readText().trim()
                    return value.toIntOrNull() ?: continue
                }
            } catch (e: IOException) {
                continue
            }
        }
        return -1
    }
}
