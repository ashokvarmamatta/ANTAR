package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraHardwareLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow

@Composable
internal fun ModesRows(info: CameraInfo) {
    val separator = stringResource(R.string.camera_list_separator)
    InfoRow(R.string.camera_label_lens_placement, info.lensFacing?.let { stringResource(facingRes(it)) })
    InfoRow(R.string.camera_label_megapixels, info.megapixels?.let { stringResource(R.string.camera_value_megapixels, it) })
    InfoRow(R.string.camera_label_hardware_level, info.hardwareLevel?.let { stringResource(hardwareLevelRes(it)) })
    InfoRow(R.string.camera_label_aberration_modes, modesText(info.aberrationModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_antibanding_modes, modesText(info.antibandingModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_ae_modes, modesText(info.autoExposureModes, separator), singleLine = false)
    InfoRow(
        R.string.camera_label_fps_ranges,
        info.targetFpsRanges?.let { ranges ->
            if (ranges.isEmpty()) {
                stringResource(R.string.camera_value_none)
            } else {
                ranges.map { stringResource(R.string.camera_value_fps_range, it.first, it.last) }.joinToString(separator)
            }
        },
        singleLine = false
    )
    InfoRow(
        R.string.camera_label_compensation_range,
        info.compensationRange?.let { stringResource(R.string.camera_value_compensation_range, it.first, it.last) }
    )
    InfoRow(
        R.string.camera_label_compensation_step,
        info.compensationStep?.let { stringResource(R.string.camera_value_fraction, it.numerator, it.denominator) }
    )
    InfoRow(R.string.camera_label_af_modes, modesText(info.autoFocusModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_effects, modesText(info.effects, separator), singleLine = false)
    InfoRow(R.string.camera_label_scene_modes, modesText(info.sceneModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_video_stabilization, modesText(info.videoStabilizationModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_awb_modes, modesText(info.autoWhiteBalanceModes, separator), singleLine = false)
}

@StringRes
private fun hardwareLevelRes(level: CameraHardwareLevel): Int = when (level) {
    CameraHardwareLevel.LEGACY -> R.string.camera_hw_level_legacy
    CameraHardwareLevel.LIMITED -> R.string.camera_hw_level_limited
    CameraHardwareLevel.FULL -> R.string.camera_hw_level_full
    CameraHardwareLevel.LEVEL_3 -> R.string.camera_hw_level_3
    CameraHardwareLevel.EXTERNAL -> R.string.camera_hw_level_external
}
