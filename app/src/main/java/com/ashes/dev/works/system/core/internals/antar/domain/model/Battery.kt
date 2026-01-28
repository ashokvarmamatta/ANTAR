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
    val designCapacity: String,
    val estimatedMaxCapacity: String,
    val remainingCapacity: String,
    val chargeCycles: String,
    val dualCellDevice: String,
    val drainedCapacity: String,
    val batteryHealthStatus: String
)
