package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Storage(
    val freeMemory: String,
    val usedTotalMemory: String,
    val usagePercentageRam: String,
    val ramType: String,
    val internalStoragePath: String,
    val usedTotalFreeInternal: String,
    val usagePercentageInternal: String,
    val systemStorageFileSystemType: String,
    val systemStoragePath: String,
    val systemStorageUsageProgress: String,
    val usedTotalFreeSystem: String,
    val internalStorageDataFileSystemType: String,
    val internalStorageDataPath: String,
    val internalStorageDataUsageProgress: String,
    val usedTotalFreeInternalData: String
)