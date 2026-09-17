package com.ashes.dev.works.system.core.internals.antar.domain.model

/** A width × height in pixels. */
data class PixelSize(val width: Int, val height: Int) {
    val pixelCount: Long get() = width.toLong() * height.toLong()
}

/** Physical sensor dimensions in millimetres. */
data class SensorSizeMm(val widthMm: Float, val heightMm: Float)

/** An exact fraction as reported by the platform (e.g. an exposure compensation step of 1/6 EV). */
data class ExposureStep(val numerator: Int, val denominator: Int)

enum class LensFacing { FRONT, BACK, EXTERNAL }

enum class CameraHardwareLevel { LEGACY, LIMITED, FULL, LEVEL_3, EXTERNAL }

/** What the output sizes suggest about pixel binning; derived from array size vs. largest JPEG. */
enum class PixelBinning {
    /** The sensor pixel array is wider than the largest output: binning is certain. */
    CONFIRMED,

    /** The largest output is ~12 MP, a typical 4-in-1 binned size. */
    LIKELY_4_IN_1_12MP,

    /** The largest output is ~16 MP, a typical 4-in-1 binned size. */
    LIKELY_4_IN_1_16MP,

    /** The OS reports what looks like the native sensor size. */
    NATIVE
}

/** The high-resolution capture path of Android 12+. */
sealed interface UltraHighResMode {
    /** The Android version is below 12, where this capability does not exist. */
    data object UnsupportedOs : UltraHighResMode

    /** The Android version supports it but this camera does not expose it. */
    data object Unavailable : UltraHighResMode

    data class Available(val maxJpegSize: PixelSize) : UltraHighResMode
}

enum class CameraCapabilityType {
    BACKWARD_COMPATIBLE,
    MANUAL_SENSOR,
    MANUAL_POST_PROCESSING,
    RAW,
    PRIVATE_REPROCESSING,
    READ_SENSOR_SETTINGS,
    BURST_CAPTURE,
    YUV_REPROCESSING,
    DEPTH_OUTPUT,
    CONSTRAINED_HIGH_SPEED_VIDEO,
    MOTION_TRACKING,
    LOGICAL_MULTI_CAMERA,
    MONOCHROME
}

sealed interface CameraCapability {
    data class Known(val type: CameraCapabilityType) : CameraCapability

    /** A capability id this app has no name for (newer or vendor-specific). */
    data class Other(val id: Int) : CameraCapability
}

enum class FocusDistanceCalibration { UNCALIBRATED, APPROXIMATE, CALIBRATED }

enum class CroppingType { CENTER_ONLY, FREEFORM }

enum class ColorFilterArrangement { RGGB, GRBG, GBRG, BGGR, RGB, MONO, NIR }

enum class SensorTimestampSource { UNKNOWN, REALTIME }

/**
 * Raw Camera2 characteristics of one camera. Every field is null when the platform does not report
 * it. For lists, null means "not reported" and an empty list means "reported, but none".
 * Mode lists hold the raw Camera2 constant ids; the UI shows them as numbers.
 */
data class CameraInfo(
    val id: String,

    // Basic
    val megapixels: Double?,
    val maxJpegSize: PixelSize?,
    val lensFacing: LensFacing?,
    val hardwareLevel: CameraHardwareLevel?,

    // Sensor forensics
    val pixelArraySize: PixelSize?,
    val rawSensorSize: PixelSize?,
    val binning: PixelBinning?,
    /** Null below Android 9, where logical multi-cameras are not exposed. */
    val physicalCameraIds: List<String>?,
    val ultraHighRes: UltraHighResMode,

    // Capabilities
    val jpegSizes: List<PixelSize>?,
    val capabilities: List<CameraCapability>?,

    // Modes & effects
    val aberrationModes: List<Int>?,
    val antibandingModes: List<Int>?,
    val autoExposureModes: List<Int>?,
    val targetFpsRanges: List<IntRange>?,
    val compensationRange: IntRange?,
    val compensationStep: ExposureStep?,
    val autoFocusModes: List<Int>?,
    val effects: List<Int>?,
    val sceneModes: List<Int>?,
    val videoStabilizationModes: List<Int>?,
    val autoWhiteBalanceModes: List<Int>?,

    // Control & hardware
    val maxAutoExposureRegions: Int?,
    val maxAutoFocusRegions: Int?,
    val maxAutoWhiteBalanceRegions: Int?,
    val edgeModes: List<Int>?,
    val flashAvailable: Boolean?,
    val hotPixelModes: List<Int>?,

    // Lens & sensor
    val thumbnailSizes: List<PixelSize>?,
    val apertures: List<Float>?,
    val filterDensities: List<Float>?,
    val focalLengthsMm: List<Float>?,
    val opticalStabilizationModes: List<Int>?,
    val focusDistanceCalibration: FocusDistanceCalibration?,
    val maxOutputStreamsProcessed: Int?,
    val maxOutputStreamsStalling: Int?,
    val maxRawOutputStreams: Int?,
    val partialResultCount: Int?,
    val maxDigitalZoom: Float?,
    val croppingType: CroppingType?,

    // Resolution & format
    val testPatternModes: List<Int>?,
    val colorFilterArrangement: ColorFilterArrangement?,
    val sensorPhysicalSize: SensorSizeMm?,
    val timestampSource: SensorTimestampSource?,
    val sensorOrientationDegrees: Int?
)
