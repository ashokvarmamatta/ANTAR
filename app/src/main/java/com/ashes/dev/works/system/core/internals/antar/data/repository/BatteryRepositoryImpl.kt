package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import java.io.File
import java.io.IOException
import java.util.Locale

class BatteryRepositoryImpl(private val context: Context) : BatteryRepository {
    override fun getBattery(): Battery {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (scale > 0) level / scale.toFloat() else 0.0f

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        val temperature = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val technology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "- - -"

        val remainingCapacityUah = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER).toLong()
        val currentNowUa = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val powerWatt = (currentNowUa / 1_000_000.0) * (voltage / 1000.0)

        val designCapacity = getDesignCapacity(context)
        val estimatedMaxCapacity = estimateMaxCapacity(context)
        val chargeCycles = getBatteryCycleCount(context)

        val drainedCapacity = if (designCapacity != -1.0 && estimatedMaxCapacity != -1) {
            (designCapacity - estimatedMaxCapacity).toInt()
        } else {
            -1
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

        return Battery(
            batteryLevel = "${(batteryPct * 100).toInt()}%",
            status = if (isCharging) "Charging" else "Discharging",
            current = "$currentNowUa μA",
            power = String.format(Locale.getDefault(), "%.2f W", powerWatt),
            temperature = "${temperature / 10f}°C",
            health = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                else -> "Unknown"
            },
            powerSource = getChargerType(context),
            technology = technology,
            voltage = "${voltage / 1000f}V",
            designCapacity = if (designCapacity != -1.0) "${designCapacity.toInt()} mAh" else "- - -",
            estimatedMaxCapacity = if (estimatedMaxCapacity != -1) "$estimatedMaxCapacity mAh" else "- - -",
            remainingCapacity = "${remainingCapacityUah / 1000} mAh",
            chargeCycles = if (chargeCycles != -1) chargeCycles.toString() else "- - -",
            drainedCapacity = if (drainedCapacity != -1) "$drainedCapacity mAh" else "- - -",
            batteryHealthStatus = batteryHealthStatus,
            dualCellDevice = "No"
        )
    }

    private fun getChargerType(context: Context): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1

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
        if (Build.VERSION.SDK_INT >= 34) { // Build.VERSION_CODES.UPSIDE_DOWN_CAKE
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
