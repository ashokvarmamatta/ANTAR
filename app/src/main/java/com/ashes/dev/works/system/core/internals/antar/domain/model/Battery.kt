package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Battery(
    val batteryLevel: Int,
    val preciseLevel: Double,
    val isCharging: Boolean,
    val current: Int, // in micro-amperes (uA)
    val power: Double, // in Watts (W)
    val temperature: Int, // in 0.1 degrees Celsius
    val health: String,
    val chargerType: String,
    val technology: String,
    val voltage: Double, // in Volts (V)
    val designCapacity: Int, // in mAh
    val estimatedMaxCapacity: Int, // in mAh
    val remainingCapacity: Int, // in microampere-hours (uAh)
    val chargeCycles: Int,
    val batteryHealthStatus: String,
    val currentHistory: List<Int> = emptyList()
)
