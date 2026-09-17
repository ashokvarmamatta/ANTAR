package com.ashes.dev.works.system.core.internals.antar.domain.model

/** Everything the Dashboard shows. Any part that could not be read is null and its card hides. */
data class DashboardSummary(
    val deviceName: String?,
    val androidVersion: String?,
    val ram: MemoryUsage?,
    val internalStorage: VolumeUsage?,
    val battery: Battery?,
    val socName: String?,
    val coreCount: Int?,
    val cpuFrequencyKhz: Long?,
    val sensorCount: Int?,
    /** Null until the user accepts the installed-apps disclosure on the Apps tab. */
    val appCount: Int?,
    val uptimeMillis: Long?
)
