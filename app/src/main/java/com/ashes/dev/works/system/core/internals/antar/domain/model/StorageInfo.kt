package com.ashes.dev.works.system.core.internals.antar.domain.model

/** Memory module standard. [token] is the industry identifier, shown as-is (not translated). */
enum class RamType(val token: String) {
    DDR3("DDR3"),
    DDR4("DDR4"),
    DDR5("DDR5"),
    LPDDR2("LPDDR2"),
    LPDDR3("LPDDR3"),
    LPDDR4("LPDDR4"),
    LPDDR4X("LPDDR4X"),
    LPDDR5("LPDDR5"),
    LPDDR5X("LPDDR5X"),
    LPDDR5T("LPDDR5T"),
    LPDDR6("LPDDR6")
}

/** Raw memory and storage facts. All sizes are bytes; the UI formats them. */
data class StorageInfo(
    val ram: MemoryUsage,
    /** Null unless the device explicitly reports the module type. Never guessed. */
    val ramType: RamType?,
    /** User-visible shared storage, usually /storage/emulated/0. */
    val internalStorage: VolumeUsage,
    /** Null when /system cannot be measured. */
    val systemPartition: VolumeUsage?,
    val dataPartition: VolumeUsage
)

data class MemoryUsage(
    val totalBytes: Long,
    val availableBytes: Long
) {
    val usedBytes: Long get() = (totalBytes - availableBytes).coerceAtLeast(0L)

    /** 0.0..1.0; 0 when the total is unknown. */
    val usedFraction: Float get() = if (totalBytes > 0L) usedBytes.toFloat() / totalBytes else 0f
}

data class VolumeUsage(
    val path: String,
    val totalBytes: Long,
    val freeBytes: Long,
    /** Kernel filesystem type from /proc/self/mounts (e.g. f2fs, ext4, erofs); null if not found. */
    val fileSystemType: String?
) {
    val usedBytes: Long get() = (totalBytes - freeBytes).coerceAtLeast(0L)

    /** 0.0..1.0; 0 when the total is unknown. */
    val usedFraction: Float get() = if (totalBytes > 0L) usedBytes.toFloat() / totalBytes else 0f
}
