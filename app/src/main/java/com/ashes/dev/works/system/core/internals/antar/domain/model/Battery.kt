package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Battery(
    val batteryLevel: String,
    val status: String,
    val current: String,
    val power: String,
    val temperature: String,
    val health: String,
    val powerSource: String,
    val technology: String,
    val voltage: String,
    val capacity: String,
    val dualCellDevice: String
)