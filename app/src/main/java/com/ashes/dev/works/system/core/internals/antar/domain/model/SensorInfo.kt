package com.ashes.dev.works.system.core.internals.antar.domain.model

/** One hardware or virtual sensor as the platform reports it. The UI names the [type]. */
data class SensorInfo(
    /** Unique within one sensor list; stable for the lifetime of the list. */
    val id: String,
    val name: String?,
    val vendor: String?,
    /** Android sensor type constant (`Sensor.TYPE_*`), including vendor-defined values. */
    val type: Int,
    /** Platform type token such as android.sensor.accelerometer; a technical id, not prose. */
    val stringType: String?,
    val version: Int,
    val powerMilliAmps: Float,
    val resolution: Float,
    val maximumRange: Float,
    val minDelayMicros: Int,
    val isWakeUp: Boolean
)
