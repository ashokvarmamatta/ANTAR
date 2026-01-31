package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.Sensors
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository

class SensorsRepositoryImpl(private val context: Context) : SensorsRepository {
    override fun getSensors(): Sensors {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val nativeSensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        
        val sensorList = nativeSensorList.map { sensor ->
            SensorDetail(
                name = sensor.name ?: "Unknown",
                vendor = sensor.vendor ?: "Unknown",
                type = getSensorTypeName(sensor.type),
                version = sensor.version,
                power = sensor.power,
                resolution = sensor.resolution,
                maximumRange = sensor.maximumRange,
                minDelay = sensor.minDelay
            )
        }.sortedBy { it.name.lowercase() }

        return Sensors(
            sensorCountMessage = "${sensorList.size} sensors available",
            sensorList = sensorList
        )
    }

    private fun getSensorTypeName(type: Int): String {
        return when (type) {
            Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ambient Temperature"
            Sensor.TYPE_GAME_ROTATION_VECTOR -> "Game Rotation Vector"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "Geomagnetic Rotation Vector"
            Sensor.TYPE_GRAVITY -> "Gravity"
            Sensor.TYPE_GYROSCOPE -> "Gyroscope"
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "Gyroscope Uncalibrated"
            Sensor.TYPE_HEART_BEAT -> "Heart Beat"
            Sensor.TYPE_HEART_RATE -> "Heart Rate"
            Sensor.TYPE_LIGHT -> "Light"
            Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
            Sensor.TYPE_MAGNETIC_FIELD -> "Magnetic Field"
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "Magnetic Field Uncalibrated"
            Sensor.TYPE_PRESSURE -> "Pressure"
            Sensor.TYPE_PROXIMITY -> "Proximity"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "Relative Humidity"
            Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
            Sensor.TYPE_SIGNIFICANT_MOTION -> "Significant Motion"
            Sensor.TYPE_STATIONARY_DETECT -> "Stationary Detect"
            Sensor.TYPE_STEP_COUNTER -> "Step Counter"
            Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
            else -> "Other ($type)"
        }
    }
}
