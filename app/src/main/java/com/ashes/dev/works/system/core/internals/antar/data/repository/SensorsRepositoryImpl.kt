package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.Sensors
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository

class SensorsRepositoryImpl(private val context: Context) : SensorsRepository {
    override fun getSensors(): Sensors {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val sensorCount = sensorList.size

        return Sensors(
            sensorCountMessage = "$sensorCount sensors available",
            sensorTypeName = "- - -",
            name = "- - -",
            vendor = "- - -",
            wakeUpSensor = "- - -",
            power = "- - -"
        )
    }
}
