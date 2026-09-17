package com.ashes.dev.works.system.core.internals.antar.presentation.camera

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.ui.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.ui.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapability
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraCapabilityType
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraHardwareLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.ColorFilterArrangement
import com.ashes.dev.works.system.core.internals.antar.domain.model.CroppingType
import com.ashes.dev.works.system.core.internals.antar.domain.model.FocusDistanceCalibration
import com.ashes.dev.works.system.core.internals.antar.domain.model.LensFacing
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelBinning
import com.ashes.dev.works.system.core.internals.antar.domain.model.PixelSize
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorTimestampSource
import com.ashes.dev.works.system.core.internals.antar.domain.model.UltraHighResMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "cameraState"
    ) { state ->
        when (state) {
            CameraUiState.Loading -> LoadingSkeleton()
            CameraUiState.Empty -> ErrorState(
                message = R.string.camera_empty,
                icon = Icons.Outlined.CameraAlt,
                onRetry = viewModel::load
            )
            is CameraUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is CameraUiState.Content -> CameraContent(
                state = state,
                onSelect = viewModel::selectCamera,
                onRetrySelected = viewModel::retrySelected
            )
        }
    }
}

@Composable
private fun CameraContent(
    state: CameraUiState.Content,
    onSelect: (String) -> Unit,
    onRetrySelected: () -> Unit
) {
    val info = state.selectedInfo
    val selectedError = state.selectedError

    AdaptiveCardGrid {
        item(key = "selector", span = StaggeredGridItemSpan.FullLine) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntry(0),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.cameraIds, key = { it }) { id ->
                    CameraCard(
                        id = id,
                        info = state.infos[id],
                        isSelected = id == state.selectedId,
                        onClick = { onSelect(id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        item(key = "notice", span = StaggeredGridItemSpan.FullLine) {
            BinningNotice(modifier = Modifier.staggeredEntry(1))
        }

        when {
            info != null -> {
                item(key = "forensics") {
                    CameraSectionCard(info, 2, R.string.camera_section_forensics, Icons.Outlined.Info, AntarOrange) {
                        ForensicsRows(it)
                    }
                }
                item(key = "modes") {
                    CameraSectionCard(info, 3, R.string.camera_section_modes, Icons.Outlined.Tune, MaterialTheme.colorScheme.primary) {
                        ModesRows(it)
                    }
                }
                item(key = "control") {
                    CameraSectionCard(info, 4, R.string.camera_section_control, Icons.Outlined.Settings, AntarBlue) {
                        ControlRows(it)
                    }
                }
                item(key = "lens") {
                    CameraSectionCard(info, 5, R.string.camera_section_lens, Icons.Outlined.Lens, AntarPurple) {
                        LensRows(it)
                    }
                }
                item(key = "resolution") {
                    CameraSectionCard(info, 6, R.string.camera_section_resolution, Icons.Outlined.PhotoCamera, AntarGreen) {
                        ResolutionRows(it)
                    }
                }
            }

            selectedError != null -> item(key = "selectedError", span = StaggeredGridItemSpan.FullLine) {
                ErrorState(
                    message = selectedError,
                    onRetry = onRetrySelected,
                    modifier = Modifier
                        .staggeredEntry(2)
                        .animateItem()
                )
            }

            else -> item(key = "selectedLoading", span = StaggeredGridItemSpan.FullLine) {
                LoadingSkeleton(
                    sections = 2,
                    modifier = Modifier
                        .staggeredEntry(2)
                        .animateItem()
                )
            }
        }
    }
}

// ── Cards ────────────────────────────────────────────────────────────

/** A section card whose rows cross-fade when a different camera is selected. */
@Composable
private fun CameraSectionCard(
    info: CameraInfo,
    index: Int,
    @StringRes title: Int,
    icon: ImageVector,
    accentColor: Color,
    rows: @Composable (CameraInfo) -> Unit
) {
    PremiumCard(modifier = Modifier.staggeredEntry(index)) {
        SectionTitle(title = title, icon = icon, accentColor = accentColor)
        AnimatedContent(
            targetState = info,
            contentKey = { it.id },
            transitionSpec = LocalAnimationIntensity.current.contentSwap(),
            label = "cameraSection"
        ) { shown ->
            Column(modifier = Modifier.fillMaxWidth()) {
                rows(shown)
            }
        }
    }
}

@Composable
private fun BinningNotice(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AntarOrange.copy(alpha = 0.1f))
            .border(1.dp, AntarOrange.copy(alpha = 0.3f), shape)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = AntarOrange,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.camera_binning_notice_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AntarOrange
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.camera_binning_notice_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CameraCard(
    id: String,
    info: CameraInfo?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val intensity = LocalAnimationIntensity.current
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AntarCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = intensity.effectsSpec(),
        label = "cameraCardBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AntarCyan.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        animationSpec = intensity.effectsSpec(),
        label = "cameraCardBorder"
    )
    val accentColor = if (isSelected) AntarCyan else AntarGray
    val shape = RoundedCornerShape(16.dp)

    val cameraLabel = stringResource(R.string.camera_card_camera_id, id)
    val megapixels = info?.megapixels?.let { stringResource(R.string.camera_value_megapixels, it) }
    val resolution = info?.maxJpegSize?.let { sizeText(it) }
    val title = megapixels ?: cameraLabel
    val subtitle = when {
        isSelected -> resolution.orEmpty()
        megapixels != null -> cameraLabel
        else -> ""
    }
    val facing = info?.lensFacing?.let { stringResource(facingRes(it)) }.orEmpty()

    Box(
        modifier = modifier
            .width(140.dp)
            .height(120.dp)
            .semantics { selected = isSelected }
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .bounceClick(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AntarCyan else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AntarGray,
                    maxLines = 1
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = facing,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = stringResource(R.string.camera_cd_selected),
                        modifier = Modifier.size(18.dp),
                        tint = AntarCyan
                    )
                }
            }
        }
    }
}

// ── Section rows ─────────────────────────────────────────────────────

@Composable
private fun ForensicsRows(info: CameraInfo) {
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

@Composable
private fun ModesRows(info: CameraInfo) {
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

@Composable
private fun ControlRows(info: CameraInfo) {
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

@Composable
private fun LensRows(info: CameraInfo) {
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
private fun ResolutionRows(info: CameraInfo) {
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

// ── Formatting ───────────────────────────────────────────────────────

@Composable
private fun sizeText(size: PixelSize): String = stringResource(R.string.camera_value_size, size.width, size.height)

/** Null when not reported, "None" when reported empty, otherwise the raw Camera2 ids joined. */
@Composable
private fun modesText(modes: List<Int>?, separator: String): String? = when {
    modes == null -> null
    modes.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> modes.joinToString(separator)
}

@Composable
private fun numbersText(numbers: List<Float>?, separator: String): String? = when {
    numbers == null -> null
    numbers.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> numbers.joinToString(separator)
}

@Composable
private fun sizesText(sizes: List<PixelSize>?, separator: String): String? = when {
    sizes == null -> null
    sizes.isEmpty() -> stringResource(R.string.camera_value_none)
    else -> sizes.map { sizeText(it) }.joinToString(separator)
}

// ── Enum → string resource ───────────────────────────────────────────

@StringRes
private fun facingRes(facing: LensFacing): Int = when (facing) {
    LensFacing.FRONT -> R.string.camera_facing_front
    LensFacing.BACK -> R.string.camera_facing_back
    LensFacing.EXTERNAL -> R.string.camera_facing_external
}

@StringRes
private fun hardwareLevelRes(level: CameraHardwareLevel): Int = when (level) {
    CameraHardwareLevel.LEGACY -> R.string.camera_hw_level_legacy
    CameraHardwareLevel.LIMITED -> R.string.camera_hw_level_limited
    CameraHardwareLevel.FULL -> R.string.camera_hw_level_full
    CameraHardwareLevel.LEVEL_3 -> R.string.camera_hw_level_3
    CameraHardwareLevel.EXTERNAL -> R.string.camera_hw_level_external
}

@StringRes
private fun binningRes(binning: PixelBinning): Int = when (binning) {
    PixelBinning.CONFIRMED -> R.string.camera_binning_confirmed
    PixelBinning.LIKELY_4_IN_1_12MP -> R.string.camera_binning_likely_12mp
    PixelBinning.LIKELY_4_IN_1_16MP -> R.string.camera_binning_likely_16mp
    PixelBinning.NATIVE -> R.string.camera_binning_native
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
