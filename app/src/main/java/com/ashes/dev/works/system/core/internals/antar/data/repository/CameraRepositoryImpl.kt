package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository
import java.util.Locale

class CameraRepositoryImpl(private val context: Context) : CameraRepository {

    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val TAG = "CameraRepo"

    override fun getCameraIds(): List<String> {
        return try {
            manager.cameraIdList.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getCamera(id: String): Camera {
        try {
            val chars = manager.getCameraCharacteristics(id)
            val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            // --- 1. SENSOR FORENSICS (The Raw Truth) ---

            // A. Active Array Size
            val activeArray = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
            val activeArrayStr = activeArray?.let { "${it.width}x${it.height}" } ?: "Unknown"

            // B. RAW_SENSOR Size
            val rawSize = map?.getOutputSizes(ImageFormat.RAW_SENSOR)?.maxByOrNull { it.width * it.height }
            val rawSizeStr = rawSize?.let { "${it.width}x${it.height}" } ?: "Not Supported"

            // C. Ultra High-Res Mode (API 31+)
            var ultraHighResStr = "Not Supported / Legacy Device"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    val highResMap = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION)
                    val highResSize = highResMap?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }
                    ultraHighResStr = highResSize?.let { "Supported: ${it.width}x${it.height}" } ?: "Not Available"
                } catch (e: Exception) {
                    ultraHighResStr = "Error/Hidden"
                }
            }

            // D. Physical Cameras (API 28+)
            val physicalIds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                chars.physicalCameraIds.joinToString(", ").ifEmpty { "None (Logical Only)" }
            } else {
                "Not Supported"
            }

            // E. Calculate "MegaPixels" based on the largest JPEG found
            val maxJpeg = map?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }
            val mp = if (maxJpeg != null) {
                val m = (maxJpeg.width.toLong() * maxJpeg.height.toLong()).toDouble() / 1_000_000.0
                String.format(Locale.US, "%.1f MP", m)
            } else {
                "Unknown"
            }

            // F. Binning Analysis
            val pixelCount = maxJpeg?.let { it.width * it.height } ?: 0
            val binningNote = when {
                (activeArray != null && maxJpeg != null && activeArray.width > maxJpeg.width) ->
                    "Confirmed (Array > Output)"
                pixelCount in 11_900_000..12_700_000 -> "Likely 4-in-1 (12MP Output)"
                pixelCount in 15_900_000..16_500_000 -> "Likely 4-in-1 (16MP Output)"
                else -> "OS Reporting Native Size"
            }

            return Camera(
                // Main Header
                megaPixels = mp,
                lensPlacement = mapLensFacing(chars.get(CameraCharacteristics.LENS_FACING)),
                hardwareLevel = mapHardwareLevel(chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)),

                // NEW: Sensor Forensics
                pixelArraySize = activeArrayStr,
                rawSensorSize = rawSizeStr,
                binningStatus = binningNote,
                physicalIds = physicalIds,
                ultraHighResMode = ultraHighResStr,

                // Standard Details
                supportedResolutions = map?.getOutputSizes(ImageFormat.JPEG)?.joinToString { "${it.width}x${it.height}" } ?: "None",
                aberrationModes = chars.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)?.let { mapModes(it) } ?: "None",
                antibandingModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)?.let { mapModes(it) } ?: "None",
                autoExposureModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)?.let { mapModes(it) } ?: "None",
                targetFpsRanges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)?.joinToString { "[${it.lower}-${it.upper}]" } ?: "None",
                compensationRange = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)?.toString() ?: "None",
                compensationStep = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP)?.toString() ?: "None",
                autoFocusModes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)?.let { mapModes(it) } ?: "None",
                effects = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)?.let { mapModes(it) } ?: "None",
                sceneModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)?.let { mapModes(it) } ?: "None",
                videoStabilizationModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)?.let { mapModes(it) } ?: "None",
                autoWhiteBalanceModes = chars.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)?.let { mapModes(it) } ?: "None",
                maxAutoExposureRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)?.toString() ?: "0",
                maxAutoFocusRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)?.toString() ?: "0",
                maxAutoWhiteBalanceRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB)?.toString() ?: "0",
                edgeModes = chars.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES)?.let { mapModes(it) } ?: "None",
                flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)?.toString() ?: "False",
                hotPixelModes = chars.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES)?.let { mapModes(it) } ?: "None",
                thumbnailSizes = chars.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES)?.joinToString { "${it.width}x${it.height}" } ?: "None",
                apertures = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)?.joinToString() ?: "Fixed",
                filterDensities = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES)?.joinToString() ?: "None",
                focalLengths = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.joinToString() ?: "Fixed",
                opticalStabilization = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.let { mapModes(it) } ?: "None",
                focusDistanceCalibration = mapFocusCalibration(chars.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION)),
                cameraCapabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.let { mapCapabilities(it) } ?: "None",
                maxOutputStreams = "Unknown",
                maxOutputStreamsStalling = "Unknown",
                maxRawOutputStreams = chars.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW)?.toString() ?: "0",
                partialResults = chars.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT)?.toString() ?: "1",
                maxDigitalZoom = chars.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?.toString() ?: "1.0",
                croppingType = mapCroppingType(chars.get(CameraCharacteristics.SCALER_CROPPING_TYPE)),
                testPatternModes = chars.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES)?.let { mapModes(it) } ?: "None",
                colorFilterArrangement = mapColorFilter(chars.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT)),
                sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)?.toString() ?: "Unknown",
                timestampSource = mapTimestampSource(chars.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)),
                orientation = chars.get(CameraCharacteristics.SENSOR_ORIENTATION)?.toString() ?: "0"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error reading camera $id", e)
            // FIXED: Return Camera object with "Error" values for ALL fields
            return Camera(
                megaPixels = "Error", lensPlacement = "Unknown", hardwareLevel = "Unknown",
                pixelArraySize = "N/A", rawSensorSize = "N/A", binningStatus = "Error", physicalIds = "Error", ultraHighResMode = "Error",
                supportedResolutions = "N/A", aberrationModes = "N/A", antibandingModes = "N/A", autoExposureModes = "N/A", targetFpsRanges = "N/A",
                compensationRange = "N/A", compensationStep = "N/A", autoFocusModes = "N/A", effects = "N/A", sceneModes = "N/A", videoStabilizationModes = "N/A",
                autoWhiteBalanceModes = "N/A", maxAutoExposureRegions = "0", maxAutoFocusRegions = "0", maxAutoWhiteBalanceRegions = "0", edgeModes = "N/A",
                flashAvailable = "False", hotPixelModes = "N/A", thumbnailSizes = "N/A", apertures = "N/A", filterDensities = "N/A", focalLengths = "N/A",
                opticalStabilization = "N/A", focusDistanceCalibration = "N/A", cameraCapabilities = "N/A", maxOutputStreams = "N/A", maxOutputStreamsStalling = "N/A",
                maxRawOutputStreams = "0", partialResults = "0", maxDigitalZoom = "1.0", croppingType = "N/A", testPatternModes = "N/A",
                colorFilterArrangement = "N/A", sensorSize = "N/A", timestampSource = "N/A", orientation = "0"
            )
        }
    }

    // --- MAPPERS ---
    private fun mapModes(modes: IntArray): String = modes.joinToString(", ")

    private fun mapHardwareLevel(level: Int?): String {
        return when (level) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "Legacy"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "Limited"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "Full"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "Level 3"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> "External"
            else -> "Unknown"
        }
    }

    private fun mapLensFacing(facing: Int?): String {
        return when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }
    }

    private fun mapFocusCalibration(cal: Int?): String {
        return when (cal) {
            CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED -> "Uncalibrated"
            CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE -> "Approximate"
            CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED -> "Calibrated"
            else -> "Unknown"
        }
    }

    private fun mapCapabilities(caps: IntArray): String {
        return caps.joinToString {
            when(it) {
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> "Backward Compatible"
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> "Manual Sensor"
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> "Manual Post-Proc"
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> "RAW"
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> "Logical Multi-Cam"
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS -> "Read Sensor Settings"
                else -> it.toString()
            }
        }
    }

    private fun mapCroppingType(type: Int?): String {
        return when (type) {
            CameraCharacteristics.SCALER_CROPPING_TYPE_CENTER_ONLY -> "Center Only"
            CameraCharacteristics.SCALER_CROPPING_TYPE_FREEFORM -> "Freeform"
            else -> "Unknown"
        }
    }

    private fun mapColorFilter(cfa: Int?): String {
        return when (cfa) {
            CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB -> "RGGB"
            CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG -> "GRBG"
            CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG -> "GBRG"
            CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR -> "BGGR"
            else -> "Unknown"
        }
    }

    private fun mapTimestampSource(source: Int?): String {
        return when (source) {
            CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN -> "Unknown"
            CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME -> "Realtime"
            else -> "Unknown"
        }
    }
}