package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Cpu(
    val socName: String,
    val cores: String,
    val frequencyRange: String,
    val processor: String,
    val struct: String,
    val frequency: String,
    val fabrication: String,
    val supportedAbis: String,
    val cpuHardware: String,
    val cpuGovernor: String,
    val procCpuinfo: String,
    val gpuRenderer: String,
    val gpuVendor: String,
    val openGlEs: String,
    val openGlExtensions: String,
    val vulkan: String,
    val gpuFrequency: String,
    val currentGpuFrequency: String
)