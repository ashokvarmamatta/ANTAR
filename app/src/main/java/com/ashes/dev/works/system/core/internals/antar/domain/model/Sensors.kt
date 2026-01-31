package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Sensors(
    val sensorCountMessage: String,
    val sensorList: List<SensorDetail>
)

data class SensorDetail(
    val name: String,
    val vendor: String,
    val type: String,
    val version: Int,
    val power: Float,
    val resolution: Float,
    val maximumRange: Float,
    val minDelay: Int
)