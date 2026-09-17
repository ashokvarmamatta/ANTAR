package com.ashes.dev.works.system.core.internals.antar.domain.model

/**
 * Raw processor and graphics facts. Units are in the field names; the UI formats and labels them.
 * Every nullable field is null when the device does not expose it.
 */
data class CpuInfo(
    /** Marketed SoC name when the platform id is a known chip, otherwise the platform id itself. */
    val socName: String?,
    /** Board platform id (`ro.board.platform`) or the `Hardware` line of /proc/cpuinfo. */
    val hardware: String?,
    val coreCount: Int,
    val minFrequencyKhz: Long?,
    val maxFrequencyKhz: Long?,
    /** Current frequency of cpu0, read fresh on every call. */
    val currentFrequencyKhz: Long?,
    /** JVM architecture token such as aarch64. */
    val architecture: String?,
    /** Process node in nanometres; only known for SoCs in the built-in table. */
    val fabricationNm: Int?,
    val supportedAbis: List<String>,
    val governor: String?,
    /** Instruction-set flags from the `Features` line of /proc/cpuinfo. */
    val features: List<String>,
    /** One entry per `processor` block of /proc/cpuinfo, in file order. */
    val cores: List<CpuCore>,
    val gpu: GpuInfo
)

data class CpuCore(
    val processor: Int,
    /** Key/value lines exactly as the kernel reports them. */
    val fields: List<CpuInfoField>
)

data class CpuInfoField(val key: String, val value: String)

data class GpuInfo(
    val renderer: String?,
    val vendor: String?,
    /** OpenGL ES version reported by the platform, e.g. 3.2. */
    val openGlEsVersion: String?,
    /** Space-separated extension list exactly as the driver reports it. */
    val openGlExtensions: String?,
    /** Vulkan hardware level (0 or 1); null when the device has no Vulkan hardware support. */
    val vulkanHardwareLevel: Int?,
    val maxFrequencyHz: Long?,
    val currentFrequencyHz: Long?
)
