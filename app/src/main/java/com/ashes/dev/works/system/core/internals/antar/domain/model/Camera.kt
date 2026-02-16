package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Camera(
    // --- Basic Info ---
    val megaPixels: String,
    val lensPlacement: String,
    val hardwareLevel: String,

    // --- NEW: Sensor Forensics ---
    val pixelArraySize: String,       // Active Array (e.g. 4000x3000)
    val rawSensorSize: String,        // Raw Sensor (e.g. 8160x6120)
    val binningStatus: String,        // Status (e.g. "Likely 4-in-1")
    val physicalIds: String,          // Physical IDs (e.g. "2, 3")
    val ultraHighResMode: String,     // API 31+ High Res Status

    // --- Capabilities ---
    val supportedResolutions: String,
    val cameraCapabilities: String,

    // --- Technical Specs ---
    val aberrationModes: String,
    val antibandingModes: String,
    val autoExposureModes: String,
    val targetFpsRanges: String,
    val compensationRange: String,
    val compensationStep: String,
    val autoFocusModes: String,
    val effects: String,
    val sceneModes: String,
    val videoStabilizationModes: String,
    val autoWhiteBalanceModes: String,
    val maxAutoExposureRegions: String,
    val maxAutoFocusRegions: String,
    val maxAutoWhiteBalanceRegions: String,
    val edgeModes: String,
    val flashAvailable: String,
    val hotPixelModes: String,
    val thumbnailSizes: String,
    val apertures: String,
    val filterDensities: String,
    val focalLengths: String,
    val opticalStabilization: String,
    val focusDistanceCalibration: String,
    val maxOutputStreams: String,
    val maxOutputStreamsStalling: String,
    val maxRawOutputStreams: String,
    val partialResults: String,
    val maxDigitalZoom: String,
    val croppingType: String,
    val testPatternModes: String,
    val colorFilterArrangement: String,
    val sensorSize: String,
    val timestampSource: String,
    val orientation: String
)