package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Dashboard(
    val deviceModel: String,
    val osVersion: String,
    val ramUsage: String,
    val ramUsagePercentage: String,
    val usedMemory: String,
    val totalMemory: String,
    val freeMemory: String,
    val socName: String,
    val coreFrequencies: List<String>,
    val storageAnalysisMessage: String,
    val internalStorageUsage: String,
    val batteryStatus: String,
    val batteryVoltage: String,
    val batteryTemp: String,
    val sensorCount: String,
    val appCount: String
)