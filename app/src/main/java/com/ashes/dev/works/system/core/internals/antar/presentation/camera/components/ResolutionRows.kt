package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.ColorFilterArrangement
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorTimestampSource
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow

@Composable
internal fun ResolutionRows(info: CameraInfo) {
    val separator = stringResource(R.string.camera_list_separator)
    InfoRow(R.string.camera_label_supported_resolutions, sizesText(info.jpegSizes, separator), singleLine = false)
    InfoRow(R.string.camera_label_test_pattern_modes, modesText(info.testPatternModes, separator), singleLine = false)
    InfoRow(R.string.camera_label_color_filter, info.colorFilterArrangement?.let { stringResource(colorFilterRes(it)) })
    InfoRow(
        R.string.camera_label_sensor_size,
        info.sensorPhysicalSize?.let { stringResource(R.string.camera_value_sensor_size, it.widthMm, it.heightMm) }
    )
    InfoRow(R.string.camera_label_timestamp_source, info.timestampSource?.let { stringResource(timestampSourceRes(it)) })
    InfoRow(
        R.string.camera_label_orientation,
        info.sensorOrientationDegrees?.let { stringResource(R.string.camera_value_degrees, it) }
    )
}

@StringRes
private fun colorFilterRes(cfa: ColorFilterArrangement): Int = when (cfa) {
    ColorFilterArrangement.RGGB -> R.string.camera_cfa_rggb
    ColorFilterArrangement.GRBG -> R.string.camera_cfa_grbg
    ColorFilterArrangement.GBRG -> R.string.camera_cfa_gbrg
    ColorFilterArrangement.BGGR -> R.string.camera_cfa_bggr
    ColorFilterArrangement.RGB -> R.string.camera_cfa_rgb
    ColorFilterArrangement.MONO -> R.string.camera_cfa_mono
    ColorFilterArrangement.NIR -> R.string.camera_cfa_nir
}

@StringRes
private fun timestampSourceRes(source: SensorTimestampSource): Int = when (source) {
    SensorTimestampSource.UNKNOWN -> R.string.common_unknown
    SensorTimestampSource.REALTIME -> R.string.camera_timestamp_realtime
}
