package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Dashboard(
    val deviceModel: String,
    val deviceName: String,
    val osVersion: String,
    val ramUsagePercentage: String,
    val usedMemory: String,
    val totalMemory: String,
    val freeMemory: String,
    val ramStatus: String,
    val internalStoragePercentage: String,
    val usedStorage: String,
    val totalStorage: String,
    val batteryStatus: String,
    val batteryTemp: String,
    val batteryVoltage: String,
    val processorName: String,
    val processorDetails: String,
    val sensorCount: String,
    val appCount: String,
    val sysHealth: String,
    val uptime: String
)
