package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapability
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapabilityType
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.CroppingType
import com.ashes.dev.works.system.core.internals.antar.domain.model.FocusDistanceCalibration
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow

@Composable
internal fun LensRows(info: CameraInfo) {
    val separator = stringResource(R.string.camera_list_separator)
    InfoRow(R.string.camera_label_thumbnail_sizes, sizesText(info.thumbnailSizes, separator), singleLine = false)
    InfoRow(R.string.camera_label_apertures, numbersText(info.apertures, separator), singleLine = false)
    InfoRow(R.string.camera_label_filter_densities, numbersText(info.filterDensities, separator), singleLine = false)
    InfoRow(R.string.camera_label_focal_lengths, numbersText(info.focalLengthsMm, separator), singleLine = false)
    InfoRow(R.string.camera_label_optical_stabilization, modesText(info.opticalStabilizationModes, separator), singleLine = false)
    InfoRow(
        R.string.camera_label_focus_calibration,
        info.focusDistanceCalibration?.let { stringResource(focusCalibrationRes(it)) }
    )
    InfoRow(
        R.string.camera_label_capabilities,
        info.capabilities?.let { capabilities ->
            if (capabilities.isEmpty()) {
                stringResource(R.string.camera_value_none)
            } else {
                capabilities.map { capability ->
                    when (capability) {
                        is CameraCapability.Known -> stringResource(capabilityRes(capability.type))
                        is CameraCapability.Other -> stringResource(R.string.camera_capability_other, capability.id)
                    }
                }.joinToString(separator)
            }
        },
        singleLine = false
    )
    InfoRow(R.string.camera_label_max_output_streams, info.maxOutputStreamsProcessed?.toString())
    InfoRow(R.string.camera_label_max_stalling_streams, info.maxOutputStreamsStalling?.toString())
    InfoRow(R.string.camera_label_max_raw_streams, info.maxRawOutputStreams?.toString())
    InfoRow(R.string.camera_label_partial_results, info.partialResultCount?.toString())
    InfoRow(R.string.camera_label_max_digital_zoom, info.maxDigitalZoom?.let { stringResource(R.string.camera_value_zoom, it) })
    InfoRow(R.string.camera_label_cropping_type, info.croppingType?.let { stringResource(croppingTypeRes(it)) })
}

@Composable
private fun numbersText(numbers: List<Float>?, separator: String): String? = when {
    numbers == null -> null
    numbers.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> numbers.joinToString(separator)
}

@StringRes
private fun capabilityRes(type: CameraCapabilityType): Int = when (type) {
    CameraCapabilityType.BACKWARD_COMPATIBLE -> R.string.camera_capability_backward_compatible
    CameraCapabilityType.MANUAL_SENSOR -> R.string.camera_capability_manual_sensor
    CameraCapabilityType.MANUAL_POST_PROCESSING -> R.string.camera_capability_manual_post_processing
    CameraCapabilityType.RAW -> R.string.camera_capability_raw
    CameraCapabilityType.PRIVATE_REPROCESSING -> R.string.camera_capability_private_reprocessing
    CameraCapabilityType.READ_SENSOR_SETTINGS -> R.string.camera_capability_read_sensor_settings
    CameraCapabilityType.BURST_CAPTURE -> R.string.camera_capability_burst_capture
    CameraCapabilityType.YUV_REPROCESSING -> R.string.camera_capability_yuv_reprocessing
    CameraCapabilityType.DEPTH_OUTPUT -> R.string.camera_capability_depth_output
    CameraCapabilityType.CONSTRAINED_HIGH_SPEED_VIDEO -> R.string.camera_capability_high_speed_video
    CameraCapabilityType.MOTION_TRACKING -> R.string.camera_capability_motion_tracking
    CameraCapabilityType.LOGICAL_MULTI_CAMERA -> R.string.camera_capability_logical_multi_camera
    CameraCapabilityType.MONOCHROME -> R.string.camera_capability_monochrome
}

@StringRes
private fun focusCalibrationRes(calibration: FocusDistanceCalibration): Int = when (calibration) {
    FocusDistanceCalibration.UNCALIBRATED -> R.string.camera_focus_uncalibrated
    FocusDistanceCalibration.APPROXIMATE -> R.string.camera_focus_approximate
    FocusDistanceCalibration.CALIBRATED -> R.string.camera_focus_calibrated
}

@StringRes
private fun croppingTypeRes(type: CroppingType): Int = when (type) {
    CroppingType.CENTER_ONLY -> R.string.camera_cropping_center_only
    CroppingType.FREEFORM -> R.string.camera_cropping_freeform
}
