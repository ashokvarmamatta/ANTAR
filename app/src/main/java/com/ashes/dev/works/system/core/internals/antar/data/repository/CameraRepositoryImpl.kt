package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Range
import android.util.Size
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapability
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapabilityType
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraHardwareLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.ColorFilterArrangement
import com.ashes.dev.works.system.core.internals.antar.domain.model.CroppingType
import com.ashes.dev.works.system.core.internals.antar.domain.model.ExposureStep
import com.ashes.dev.works.system.core.internals.antar.domain.model.FocusDistanceCalibration
import com.ashes.dev.works.system.core.internals.antar.domain.model.LensFacing
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelBinning
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelSize
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorSizeMm
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorTimestampSource
import com.ashes.dev.works.system.core.internals.antar.domain.model.UltraHighResMode
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/** Reads Camera2 characteristics only; never opens a camera, so no permission is needed. */
class CameraRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : CameraRepository {

    private val manager: CameraManager
        get() = context.getSystemService(CameraManager::class.java)

    override suspend fun getCameraIds(): AppResult<List<String>> = withContext(io) {
        appResultOf { manager.cameraIdList.toList() }
    }

    override suspend fun getCameraInfo(id: String): AppResult<CameraInfo> = withContext(io) {
        appResultOf {
            val chars = manager.getCameraCharacteristics(id)
            val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val jpegSizes = map?.getOutputSizes(ImageFormat.JPEG)?.map { it.toPixelSize() }
            val maxJpeg = jpegSizes?.maxByOrNull { it.pixelCount }
            val pixelArray = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)?.toPixelSize()
            val rawSize = map?.getOutputSizes(ImageFormat.RAW_SENSOR)
                ?.maxByOrNull { it.width.toLong() * it.height.toLong() }
                ?.toPixelSize()

            CameraInfo(
                id = id,
                megapixels = maxJpeg?.let { it.pixelCount / PIXELS_PER_MEGAPIXEL },
                maxJpegSize = maxJpeg,
                lensFacing = lensFacing(chars.get(CameraCharacteristics.LENS_FACING)),
                hardwareLevel = hardwareLevel(chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)),

                pixelArraySize = pixelArray,
                rawSensorSize = rawSize,
                binning = binning(pixelArray, maxJpeg),
                physicalCameraIds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    chars.physicalCameraIds.toList()
                } else {
                    null
                },
                ultraHighRes = ultraHighRes(chars),

                jpegSizes = jpegSizes,
                capabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                    ?.map { capability(it) },

                aberrationModes = chars.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)?.toList(),
                antibandingModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)?.toList(),
                autoExposureModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)?.toList(),
                targetFpsRanges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
                    ?.map { it.toIntRange() },
                compensationRange = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)?.toIntRange(),
                compensationStep = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP)
                    ?.let { ExposureStep(it.numerator, it.denominator) },
                autoFocusModes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)?.toList(),
                effects = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)?.toList(),
                sceneModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)?.toList(),
                videoStabilizationModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)?.toList(),
                autoWhiteBalanceModes = chars.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)?.toList(),

                maxAutoExposureRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE),
                maxAutoFocusRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF),
                maxAutoWhiteBalanceRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB),
                edgeModes = chars.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES)?.toList(),
                flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE),
                hotPixelModes = chars.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES)?.toList(),

                thumbnailSizes = chars.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES)
                    ?.map { it.toPixelSize() },
                apertures = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)?.toList(),
                filterDensities = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES)?.toList(),
                focalLengthsMm = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.toList(),
                opticalStabilizationModes = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.toList(),
                focusDistanceCalibration = focusCalibration(
                    chars.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION)
                ),
                maxOutputStreamsProcessed = chars.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC),
                maxOutputStreamsStalling = chars.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING),
                maxRawOutputStreams = chars.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW),
                partialResultCount = chars.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT),
                maxDigitalZoom = chars.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM),
                croppingType = croppingType(chars.get(CameraCharacteristics.SCALER_CROPPING_TYPE)),

                testPatternModes = chars.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES)?.toList(),
                colorFilterArrangement = colorFilter(chars.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT)),
                sensorPhysicalSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
                    ?.let { SensorSizeMm(it.width, it.height) },
                timestampSource = timestampSource(chars.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)),
                sensorOrientationDegrees = chars.get(CameraCharacteristics.SENSOR_ORIENTATION)
            )
        }
    }

    // ── Mappers: platform constants → typed domain facts ─────────────────

    private fun Size.toPixelSize() = PixelSize(width, height)

    private fun Range<Int>.toIntRange(): IntRange = lower..upper

    private fun binning(pixelArray: PixelSize?, maxJpeg: PixelSize?): PixelBinning? {
        if (maxJpeg == null) return null
        return when {
            pixelArray != null && pixelArray.width > maxJpeg.width -> PixelBinning.CONFIRMED
            maxJpeg.pixelCount in BINNED_12MP_RANGE -> PixelBinning.LIKELY_4_IN_1_12MP
            maxJpeg.pixelCount in BINNED_16MP_RANGE -> PixelBinning.LIKELY_4_IN_1_16MP
            else -> PixelBinning.NATIVE
        }
    }

    private fun ultraHighRes(chars: CameraCharacteristics): UltraHighResMode {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return UltraHighResMode.UnsupportedOs
        val size = try {
            chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION)
                ?.getOutputSizes(ImageFormat.JPEG)
                ?.maxByOrNull { it.width.toLong() * it.height.toLong() }
                ?.toPixelSize()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Some vendors hide or break this key; treat it as not exposed.
            null
        }
        return size?.let { UltraHighResMode.Available(it) } ?: UltraHighResMode.Unavailable
    }

    private fun hardwareLevel(level: Int?): CameraHardwareLevel? = when (level) {
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> CameraHardwareLevel.LEGACY
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> CameraHardwareLevel.LIMITED
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> CameraHardwareLevel.FULL
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> CameraHardwareLevel.LEVEL_3
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> CameraHardwareLevel.EXTERNAL
        else -> null
    }

    private fun lensFacing(facing: Int?): LensFacing? = when (facing) {
        CameraCharacteristics.LENS_FACING_FRONT -> LensFacing.FRONT
        CameraCharacteristics.LENS_FACING_BACK -> LensFacing.BACK
        CameraCharacteristics.LENS_FACING_EXTERNAL -> LensFacing.EXTERNAL
        else -> null
    }

    private fun focusCalibration(calibration: Int?): FocusDistanceCalibration? = when (calibration) {
        CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED -> FocusDistanceCalibration.UNCALIBRATED
        CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE -> FocusDistanceCalibration.APPROXIMATE
        CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED -> FocusDistanceCalibration.CALIBRATED
        else -> null
    }

    private fun capability(id: Int): CameraCapability {
        val type = when (id) {
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> CameraCapabilityType.BACKWARD_COMPATIBLE
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> CameraCapabilityType.MANUAL_SENSOR
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> CameraCapabilityType.MANUAL_POST_PROCESSING
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> CameraCapabilityType.RAW
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING -> CameraCapabilityType.PRIVATE_REPROCESSING
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS -> CameraCapabilityType.READ_SENSOR_SETTINGS
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE -> CameraCapabilityType.BURST_CAPTURE
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING -> CameraCapabilityType.YUV_REPROCESSING
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT -> CameraCapabilityType.DEPTH_OUTPUT
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO -> CameraCapabilityType.CONSTRAINED_HIGH_SPEED_VIDEO
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING -> CameraCapabilityType.MOTION_TRACKING
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> CameraCapabilityType.LOGICAL_MULTI_CAMERA
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME -> CameraCapabilityType.MONOCHROME
            else -> null
        }
        return type?.let { CameraCapability.Known(it) } ?: CameraCapability.Other(id)
    }

    private fun croppingType(type: Int?): CroppingType? = when (type) {
        CameraCharacteristics.SCALER_CROPPING_TYPE_CENTER_ONLY -> CroppingType.CENTER_ONLY
        CameraCharacteristics.SCALER_CROPPING_TYPE_FREEFORM -> CroppingType.FREEFORM
        else -> null
    }

    private fun colorFilter(cfa: Int?): ColorFilterArrangement? = when (cfa) {
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB -> ColorFilterArrangement.RGGB
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG -> ColorFilterArrangement.GRBG
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG -> ColorFilterArrangement.GBRG
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR -> ColorFilterArrangement.BGGR
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB -> ColorFilterArrangement.RGB
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO -> ColorFilterArrangement.MONO
        CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR -> ColorFilterArrangement.NIR
        else -> null
    }

    private fun timestampSource(source: Int?): SensorTimestampSource? = when (source) {
        CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN -> SensorTimestampSource.UNKNOWN
        CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME -> SensorTimestampSource.REALTIME
        else -> null
    }

    private companion object {
        const val PIXELS_PER_MEGAPIXEL = 1_000_000.0
        val BINNED_12MP_RANGE = 11_900_000L..12_700_000L
        val BINNED_16MP_RANGE = 15_900_000L..16_500_000L
    }
}
