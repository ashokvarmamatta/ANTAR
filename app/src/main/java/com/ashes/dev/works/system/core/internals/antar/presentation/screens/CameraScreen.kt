package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CameraViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()) {
    val camera = viewModel.getCamera()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Camera Selector Header
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.cameraIds) { id ->
                    val isSelected = viewModel.selectedCameraId == id

                    CameraHeaderCard(
                        id = id,
                        isSelected = isSelected,
                        megaPixels = if (isSelected) camera.megaPixels else "Cam $id",
                        resolution = if (isSelected) camera.supportedResolutions.split(",").firstOrNull() ?: "" else "",
                        placement = if (id == "0" || id == "2") "Back" else "Front",
                        onClick = { viewModel.selectCamera(id) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- NEW: SENSOR FORENSICS CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sensor Forensics (Raw Data)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow1("OS Reported Active Array", camera.pixelArraySize)
                    InfoRow1("RAW_SENSOR Support", camera.rawSensorSize)
                    InfoRow1("Pixel Binning Status", camera.binningStatus)
                    InfoRow1("Physical Camera IDs", camera.physicalIds)
                    InfoRow1("Ultra High-Res Mode (API 31+)", camera.ultraHighResMode)

                    if (camera.binningStatus.contains("Likely")) {
                        Text(
                            text = "Note: OS is reporting 'binned' resolution as maximum.",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Modes & Effects Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Modes & Effects",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
                    InfoRow1("Video Stabilization Modes", camera.videoStabilizationModes, singleLine = false)
                    InfoRow1("Auto White Balance Modes", camera.autoWhiteBalanceModes, singleLine = false)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Control Regions & Hardware Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Control Regions & Hardware",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow1("Maximum Auto Exposure Regions", camera.maxAutoExposureRegions)
                    InfoRow1("Maximum Auto Focus Regions", camera.maxAutoFocusRegions)
                    InfoRow1("Maximum Auto White Balance Regions", camera.maxAutoWhiteBalanceRegions)
                    InfoRow1("Edge Modes", camera.edgeModes, singleLine = false)
                    InfoRow1("Flash Available", camera.flashAvailable)
                    InfoRow1("Hot Pixel Modes", camera.hotPixelModes, singleLine = false)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Lens & Sensor Specs Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lens & Sensor Specs",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow1("Thumbnail Sizes", camera.thumbnailSizes, singleLine = false)
                    InfoRow1("Apertures", camera.apertures, singleLine = false)
                    InfoRow1("Filter Densities", camera.filterDensities, singleLine = false)
                    InfoRow1("Focal Lengths", camera.focalLengths, singleLine = false)
                    InfoRow1("Optical Stabilization", camera.opticalStabilization, singleLine = false)
                    InfoRow1("Focus Distance Calibration", camera.focusDistanceCalibration)
                    InfoRow1("Camera Capabilities", camera.cameraCapabilities, singleLine = false)
                    InfoRow1("Maximum Output Streams", camera.maxOutputStreams)
                    InfoRow1("Maximum Output Streams Stalling", camera.maxOutputStreamsStalling)
                    InfoRow1("Maximum RAW Output Streams", camera.maxRawOutputStreams)
                    InfoRow1("Partial Results", camera.partialResults)
                    InfoRow1("Maximum Digital Zoom", camera.maxDigitalZoom)
                    InfoRow1("Cropping Type", camera.croppingType)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Resolution & Format Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resolution & Format",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow1("Supported Resolutions", camera.supportedResolutions, singleLine = false)
                    InfoRow1("Test Pattern Modes", camera.testPatternModes, singleLine = false)
                    InfoRow1("Color Filter Arrangement", camera.colorFilterArrangement)
                    InfoRow1("Sensor Size (Physical)", camera.sensorSize, singleLine = false)
                    InfoRow1("Timestamp Source", camera.timestampSource)
                    InfoRow1("Orientation", camera.orientation)
                }
            }
        }
    }
}

// Helper Composable for Rows
@Composable
fun InfoRow1(label: String, value: String, singleLine: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (singleLine) 1 else Int.MAX_VALUE
        )
    }
}

@Composable
private fun CameraHeaderCard(
    id: String,
    isSelected: Boolean,
    megaPixels: String,
    resolution: String,
    placement: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF90CAF9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = if (isSelected) "$megaPixels - $placement" else "Camera $id",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = if (isSelected) resolution else placement,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.Black.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val iconRes = if (placement.contains("Front", ignoreCase = true)) {
                    R.drawable.ic_camera_front
                } else {
                    R.drawable.ic_camera_back
                }

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (isSelected) Color(0xFF0D47A1) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.BottomEnd),
                        tint = Color(0xFF0D47A1)
                    )
                }
            }
        }
    }
}