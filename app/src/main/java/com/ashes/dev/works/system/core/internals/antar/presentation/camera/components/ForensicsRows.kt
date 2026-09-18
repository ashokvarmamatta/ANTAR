package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelBinning
import com.ashes.dev.works.system.core.internals.antar.domain.model.UltraHighResMode
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow

// ── Section rows ─────────────────────────────────────────────────────

@Composable
internal fun ForensicsRows(info: CameraInfo) {
    InfoRow(R.string.camera_label_active_array, info.pixelArraySize?.let { sizeText(it) })
    InfoRow(
        R.string.camera_label_raw_sensor,
        info.rawSensorSize?.let { sizeText(it) } ?: stringResource(R.string.common_not_supported)
    )
    InfoRow(R.string.camera_label_binning, info.binning?.let { stringResource(binningRes(it)) })
    InfoRow(
        R.string.camera_label_physical_ids,
        when {
            info.physicalCameraIds == null -> stringResource(R.string.common_not_supported)
            info.physicalCameraIds.isEmpty() -> stringResource(R.string.camera_value_logical_only)
            else -> info.physicalCameraIds.joinToString(stringResource(R.string.camera_list_separator))
        },
        singleLine = false
    )
    InfoRow(
        R.string.camera_label_ultra_high_res,
        when (val mode = info.ultraHighRes) {
            UltraHighResMode.UnsupportedOs -> stringResource(R.string.camera_value_ultra_high_res_unsupported_os)
            UltraHighResMode.Unavailable -> stringResource(R.string.camera_value_not_available)
            is UltraHighResMode.Available -> stringResource(R.string.camera_value_supported_size, sizeText(mode.maxJpegSize))
        },
        singleLine = false
    )

    if (info.binning == PixelBinning.LIKELY_4_IN_1_12MP || info.binning == PixelBinning.LIKELY_4_IN_1_16MP) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.camera_binning_note),
            style = MaterialTheme.typography.labelSmall,
            color = AntarOrange.copy(alpha = 0.7f)
        )
    }
}

@StringRes
private fun binningRes(binning: PixelBinning): Int = when (binning) {
    PixelBinning.CONFIRMED -> R.string.camera_binning_confirmed
    PixelBinning.LIKELY_4_IN_1_12MP -> R.string.camera_binning_likely_12mp
    PixelBinning.LIKELY_4_IN_1_16MP -> R.string.camera_binning_likely_16mp
    PixelBinning.NATIVE -> R.string.camera_binning_native
}
