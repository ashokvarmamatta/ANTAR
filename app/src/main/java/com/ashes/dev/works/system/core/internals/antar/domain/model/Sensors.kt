package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Sensors(
    val sensorCountMessage: String,
    val sensorTypeName: String,
    val name: String,
    val vendor: String,
    val wakeUpSensor: String,
    val power: String
)