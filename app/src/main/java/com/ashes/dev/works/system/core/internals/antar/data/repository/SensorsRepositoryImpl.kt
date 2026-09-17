package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.ashes.dev.works.system.core.internals.antar.core.common.AppError
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SensorsRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : SensorsRepository {

    override suspend fun getSensors(): AppResult<List<SensorInfo>> = withContext(io) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
            ?: return@withContext AppResult.Failure(AppError.Unavailable)

        appResultOf {
            val seen = HashMap<String, Int>()
            sensorManager.getSensorList(Sensor.TYPE_ALL)
                .sortedBy { it.name?.lowercase().orEmpty() }
                .map { sensor ->
                    val base = "${sensor.type}|${sensor.name}|${sensor.vendor}|${sensor.isWakeUpSensor}"
                    val occurrence = seen.getOrElse(base) { 0 }
                    seen[base] = occurrence + 1
                    SensorInfo(
                        // Identical duplicates exist on some devices; the suffix keeps list keys unique.
                        id = if (occurrence == 0) base else "$base#$occurrence",
                        name = sensor.name?.takeIf { it.isNotBlank() },
                        vendor = sensor.vendor?.takeIf { it.isNotBlank() },
                        type = sensor.type,
                        stringType = sensor.stringType?.takeIf { it.isNotBlank() },
                        version = sensor.version,
                        powerMilliAmps = sensor.power,
                        resolution = sensor.resolution,
                        maximumRange = sensor.maximumRange,
                        minDelayMicros = sensor.minDelay,
                        isWakeUp = sensor.isWakeUpSensor
                    )
                }
        }
    }
}
