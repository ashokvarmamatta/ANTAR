package com.ashes.dev.works.system.core.internals.antar.data.worker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLog
import com.ashes.dev.works.system.core.internals.antar.data.db.BatteryLogDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class BatteryLogWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val dao: BatteryLogDao by inject()

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                ?: return Result.retry()

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
            dao.insert(log)

            // Purge logs older than 30 days
            val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            dao.deleteOlderThan(thirtyDaysAgo)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
