package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Battery(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val current: Int,
    val power: Double,
    val temperature: Int,
    val health: String,
    val chargerType: String,
    val technology: String,
    val voltage: Double,
    val designCapacity: Int,
    val estimatedMaxCapacity: Int,
    val remainingCapacity: Int,
    val chargeCycles: Int,
    val batteryHealthStatus: String,
    val currentHistory: List<Int> = emptyList()
)
