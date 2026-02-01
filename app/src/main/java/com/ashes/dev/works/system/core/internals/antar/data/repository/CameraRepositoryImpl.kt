package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository

class CameraRepositoryImpl(private val context: Context) : CameraRepository {
    override fun getCamera(): Camera {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val ids = manager.cameraIdList
        if (ids.isEmpty()) {
            return emptyCamera()
        }

        val id = ids[0]
        val chars = manager.getCameraCharacteristics(id)

        return Camera(
            aberrationModes = chars.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES)?.joinToString() ?: "- - -",
            antibandingModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES)?.joinToString() ?: "- - -",
            autoExposureModes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)?.joinToString() ?: "- - -",
            targetFpsRanges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)?.joinToString() ?: "- - -",
            compensationRange = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)?.toString() ?: "- - -",
            compensationStep = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP)?.toString() ?: "- - -",
            autoFocusModes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)?.joinToString() ?: "- - -",
            effects = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)?.joinToString() ?: "- - -",
            sceneModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)?.joinToString() ?: "- - -",
            videoStabilizationModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)?.joinToString() ?: "- - -",
            autoWhiteBalanceModes = chars.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)?.joinToString() ?: "- - -",
            maxAutoExposureRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)?.toString() ?: "- - -",
            maxAutoFocusRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)?.toString() ?: "- - -",
            maxAutoWhiteBalanceRegions = chars.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB)?.toString() ?: "- - -",
            edgeModes = chars.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES)?.joinToString() ?: "- - -",
            flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)?.toString() ?: "- - -",
            hotPixelModes = chars.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES)?.joinToString() ?: "- - -",
            hardwareLevel = when (chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)) {
                CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "Legacy"
                CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> "External"
                CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "Limited"
                CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "Full"
                CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "Level 3"
                else -> "Unknown"
            },
            thumbnailSizes = chars.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES)?.joinToString() ?: "- - -",
            lensPlacement = when (chars.get(CameraCharacteristics.LENS_FACING)) {
                CameraMetadata.LENS_FACING_FRONT -> "Front"
                CameraMetadata.LENS_FACING_BACK -> "Back"
                CameraMetadata.LENS_FACING_EXTERNAL -> "External"
                else -> "Unknown"
            },
            apertures = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)?.joinToString() ?: "- - -",
            filterDensities = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES)?.joinToString() ?: "- - -",
            focalLengths = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.joinToString() ?: "- - -",
            opticalStabilization = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.joinToString() ?: "- - -",
            focusDistanceCalibration = when (chars.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION)) {
                CameraMetadata.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED -> "Uncalibrated"
                CameraMetadata.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE -> "Approximate"
                CameraMetadata.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED -> "Calibrated"
                else -> "Unknown"
            },
            cameraCapabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.joinToString() ?: "- - -",
            maxOutputStreams = "- - -",
            maxOutputStreamsStalling = "- - -",
            maxRawOutputStreams = chars.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW)?.toString() ?: "- - -",
            partialResults = chars.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT)?.toString() ?: "- - -",
            maxDigitalZoom = chars.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?.toString() ?: "- - -",
            croppingType = when (chars.get(CameraCharacteristics.SCALER_CROPPING_TYPE)) {
                CameraMetadata.SCALER_CROPPING_TYPE_CENTER_ONLY -> "Center Only"
                CameraMetadata.SCALER_CROPPING_TYPE_FREEFORM -> "Freeform"
                else -> "Unknown"
            },
            supportedResolutions = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(android.graphics.ImageFormat.JPEG)
                ?.joinToString { "${it.width}x${it.height}" } ?: "- - -",
            testPatternModes = chars.get(CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES)?.joinToString() ?: "- - -",
            colorFilterArrangement = when (chars.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT)) {
                CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB -> "RGGB"
                CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG -> "GRBG"
                CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG -> "GBRG"
                CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR -> "BGGR"
                CameraMetadata.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB -> "RGB"
                else -> "Unknown"
            },
            sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)?.toString() ?: "- - -",
            pixelArraySize = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)?.toString() ?: "- - -",
            timestampSource = when (chars.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)) {
                CameraMetadata.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN -> "Unknown"
                CameraMetadata.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME -> "Realtime"
                else -> "Unknown"
            },
            orientation = chars.get(CameraCharacteristics.SENSOR_ORIENTATION)?.toString() ?: "- - -"
        )
    }

    private fun emptyCamera() = Camera(
        aberrationModes = "- - -", antibandingModes = "- - -", autoExposureModes = "- - -",
        targetFpsRanges = "- - -", compensationRange = "- - -", compensationStep = "- - -",
        autoFocusModes = "- - -", effects = "- - -", sceneModes = "- - -",
        videoStabilizationModes = "- - -", autoWhiteBalanceModes = "- - -",
        maxAutoExposureRegions = "- - -", maxAutoFocusRegions = "- - -",
        maxAutoWhiteBalanceRegions = "- - -", edgeModes = "- - -", flashAvailable = "- - -",
        hotPixelModes = "- - -", hardwareLevel = "- - -", thumbnailSizes = "- - -",
        lensPlacement = "- - -", apertures = "- - -", filterDensities = "- - -",
        focalLengths = "- - -", opticalStabilization = "- - -", focusDistanceCalibration = "- - -",
        cameraCapabilities = "- - -", maxOutputStreams = "- - -", maxOutputStreamsStalling = "- - -",
        maxRawOutputStreams = "- - -", partialResults = "- - -", maxDigitalZoom = "- - -",
        croppingType = "- - -", supportedResolutions = "- - -", testPatternModes = "- - -",
        colorFilterArrangement = "- - -", sensorSize = "- - -", pixelArraySize = "- - -",
        timestampSource = "- - -", orientation = "- - -"
    )
}
