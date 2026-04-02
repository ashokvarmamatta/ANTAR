package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CameraViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()) {
    val camera = viewModel.getCamera()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Camera Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.cameraIds) { id ->
                    val isSelected = viewModel.selectedCameraId == id
                    val placement = if (id == "0" || id == "2") "Back" else "Front"

                    CameraCard(
                        id = id,
                        isSelected = isSelected,
                        megaPixels = if (isSelected) camera.megaPixels else "Cam $id",
                        resolution = if (isSelected) camera.supportedResolutions.split(",").firstOrNull() ?: "" else "",
                        placement = placement,
                        onClick = { viewModel.selectCamera(id) }
                    )
                }
            }
        }

        // Sensor Forensics
        item {
            PremiumCard {
                SectionTitle(title = "Sensor Forensics", icon = Icons.Outlined.Info, accentColor = AntarOrange)
                InfoRow1("OS Reported Active Array", camera.pixelArraySize)
                InfoRow1("RAW_SENSOR Support", camera.rawSensorSize)
                InfoRow1("Pixel Binning Status", camera.binningStatus)
                InfoRow1("Physical Camera IDs", camera.physicalIds)
                InfoRow1("Ultra High-Res Mode (API 31+)", camera.ultraHighResMode)

                if (camera.binningStatus.contains("Likely")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: OS is reporting 'binned' resolution as maximum.",
                        style = MaterialTheme.typography.labelSmall,
                        color = AntarOrange.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Modes & Effects", icon = Icons.Outlined.Tune)
                InfoRow1("Lens Placement", camera.lensPlacement)
                InfoRow1("Megapixels (Output)", camera.megaPixels)
                InfoRow1("Hardware Level", camera.hardwareLevel)
                InfoRow1("Aberration Modes", camera.aberrationModes, singleLine = false)
                InfoRow1("Antibanding Modes", camera.antibandingModes, singleLine = false)
                InfoRow1("Auto Exposure Modes", camera.autoExposureModes, singleLine = false)
                InfoRow1("Target FPS Ranges", camera.targetFpsRanges, singleLine = false)
                InfoRow1("Compensation Range", camera.compensationRange, singleLine = false)
                InfoRow1("Compensation Step", camera.compensationStep)
                InfoRow1("AutoFocus Modes", camera.autoFocusModes, singleLine = false)
                InfoRow1("Effects", camera.effects, singleLine = false)
                InfoRow1("Scene Modes", camera.sceneModes, singleLine = false)
                InfoRow1("Video Stabilization", camera.videoStabilizationModes, singleLine = false)
                InfoRow1("Auto White Balance", camera.autoWhiteBalanceModes, singleLine = false)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Control & Hardware", icon = Icons.Outlined.Settings, accentColor = AntarBlue)
                InfoRow1("Max AE Regions", camera.maxAutoExposureRegions)
                InfoRow1("Max AF Regions", camera.maxAutoFocusRegions)
                InfoRow1("Max AWB Regions", camera.maxAutoWhiteBalanceRegions)
                InfoRow1("Edge Modes", camera.edgeModes, singleLine = false)
                InfoRow1("Flash Available", camera.flashAvailable)
                InfoRow1("Hot Pixel Modes", camera.hotPixelModes, singleLine = false)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Lens & Sensor", icon = Icons.Outlined.Lens, accentColor = AntarPurple)
                InfoRow1("Thumbnail Sizes", camera.thumbnailSizes, singleLine = false)
                InfoRow1("Apertures", camera.apertures, singleLine = false)
                InfoRow1("Filter Densities", camera.filterDensities, singleLine = false)
                InfoRow1("Focal Lengths", camera.focalLengths, singleLine = false)
                InfoRow1("Optical Stabilization", camera.opticalStabilization, singleLine = false)
                InfoRow1("Focus Distance Calibration", camera.focusDistanceCalibration)
                InfoRow1("Camera Capabilities", camera.cameraCapabilities, singleLine = false)
                InfoRow1("Max Output Streams", camera.maxOutputStreams)
                InfoRow1("Max Stalling Streams", camera.maxOutputStreamsStalling)
                InfoRow1("Max RAW Streams", camera.maxRawOutputStreams)
                InfoRow1("Partial Results", camera.partialResults)
                InfoRow1("Max Digital Zoom", camera.maxDigitalZoom)
                InfoRow1("Cropping Type", camera.croppingType)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Resolution & Format", icon = Icons.Outlined.PhotoCamera, accentColor = AntarGreen)
                InfoRow1("Supported Resolutions", camera.supportedResolutions, singleLine = false)
                InfoRow1("Test Pattern Modes", camera.testPatternModes, singleLine = false)
                InfoRow1("Color Filter", camera.colorFilterArrangement)
                InfoRow1("Sensor Size (Physical)", camera.sensorSize, singleLine = false)
                InfoRow1("Timestamp Source", camera.timestampSource)
                InfoRow1("Orientation", camera.orientation)
            }
        }
    }
}

@Composable
fun InfoRow1(label: String, value: String, singleLine: Boolean = true) {
    if (value.isBlank() || value == "- - -") return

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE
        )
    }
}

@Composable
private fun CameraCard(
    id: String,
    isSelected: Boolean,
    megaPixels: String,
    resolution: String,
    placement: String,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) AntarCyan.copy(alpha = 0.12f) else AntarCard.copy(alpha = 0.5f)
    val borderColor = if (isSelected) AntarCyan.copy(alpha = 0.4f) else AntarDimGray.copy(alpha = 0.15f)
    val accentColor = if (isSelected) AntarCyan else AntarGray

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isSelected) "$megaPixels" else "Camera $id",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AntarCyan else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = if (isSelected) resolution else placement,
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
                    text = placement,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Selected",
                        modifier = Modifier.size(18.dp),
                        tint = AntarCyan
                    )
                }
            }
        }
    }
}
