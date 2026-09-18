package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow

@Composable
internal fun ControlRows(info: CameraInfo) {
    val separator = stringResource(R.string.camera_list_separator)
    InfoRow(R.string.camera_label_max_ae_regions, info.maxAutoExposureRegions?.toString())
    InfoRow(R.string.camera_label_max_af_regions, info.maxAutoFocusRegions?.toString())
    InfoRow(R.string.camera_label_max_awb_regions, info.maxAutoWhiteBalanceRegions?.toString())
    InfoRow(R.string.camera_label_edge_modes, modesText(info.edgeModes, separator), singleLine = false)
    InfoRow(
        R.string.camera_label_flash_available,
        info.flashAvailable?.let { stringResource(if (it) R.string.common_yes else R.string.common_no) }
    )
    InfoRow(R.string.camera_label_hot_pixel_modes, modesText(info.hotPixelModes, separator), singleLine = false)
}
