package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
                        megaPixels = if (isSelected) camera.megaPixels else "Camera", 
                        resolution = if (isSelected) camera.supportedResolutions.split(",").firstOrNull() ?: "" else "ID: $id",
                        placement = if (id == "0" || id == "2") "Back" else "Front",
                        onClick = { viewModel.selectCamera(id) }
                    )
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
                    InfoRow("Lens Placement", camera.lensPlacement)
                    InfoRow("Megapixels", camera.megaPixels)
                    InfoRow("Hardware Level", camera.hardwareLevel)
                    InfoRow("Aberration Modes", camera.aberrationModes, singleLine = false)
                    InfoRow("Antibanding Modes", camera.antibandingModes, singleLine = false)
                    InfoRow("Auto Exposure Modes", camera.autoExposureModes, singleLine = false)
                    InfoRow("Target FPS Ranges", camera.targetFpsRanges, singleLine = false)
                    InfoRow("Compensation Range", camera.compensationRange, singleLine = false)
                    InfoRow("Compensation Step", camera.compensationStep)
                    InfoRow("AutoFocus Modes", camera.autoFocusModes, singleLine = false)
                    InfoRow("Effects", camera.effects, singleLine = false)
                    InfoRow("Scene Modes", camera.sceneModes, singleLine = false)
                    InfoRow("Video Stabilization Modes", camera.videoStabilizationModes, singleLine = false)
                    InfoRow("Auto White Balance Modes", camera.autoWhiteBalanceModes, singleLine = false)
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
                    InfoRow("Maximum Auto Exposure Regions", camera.maxAutoExposureRegions)
                    InfoRow("Maximum Auto Focus Regions", camera.maxAutoFocusRegions)
                    InfoRow("Maximum Auto White Balance Regions", camera.maxAutoWhiteBalanceRegions)
                    InfoRow("Edge Modes", camera.edgeModes, singleLine = false)
                    InfoRow("Flash Available", camera.flashAvailable)
                    InfoRow("Hot Pixel Modes", camera.hotPixelModes, singleLine = false)
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
                    InfoRow("Thumbnail Sizes", camera.thumbnailSizes, singleLine = false)
                    InfoRow("Apertures", camera.apertures, singleLine = false)
                    InfoRow("Filter Densities", camera.filterDensities, singleLine = false)
                    InfoRow("Focal Lengths", camera.focalLengths, singleLine = false)
                    InfoRow("Optical Stabilization", camera.opticalStabilization, singleLine = false)
                    InfoRow("Focus Distance Calibration", camera.focusDistanceCalibration)
                    InfoRow("Camera Capabilities", camera.cameraCapabilities, singleLine = false)
                    InfoRow("Maximum Output Streams", camera.maxOutputStreams)
                    InfoRow("Maximum Output Streams Stalling", camera.maxOutputStreamsStalling)
                    InfoRow("Maximum RAW Output Streams", camera.maxRawOutputStreams)
                    InfoRow("Partial Results", camera.partialResults)
                    InfoRow("Maximum Digital Zoom", camera.maxDigitalZoom)
                    InfoRow("Cropping Type", camera.croppingType)
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
                    InfoRow("Supported Resolutions", camera.supportedResolutions, singleLine = false)
                    InfoRow("Test Pattern Modes", camera.testPatternModes, singleLine = false)
                    InfoRow("Color Filter Arrangement", camera.colorFilterArrangement)
                    InfoRow("Sensor Size", camera.sensorSize, singleLine = false)
                    InfoRow("Pixel Array Size", camera.pixelArraySize, singleLine = false)
                    InfoRow("Timestamp Source", camera.timestampSource)
                    InfoRow("Orientation", camera.orientation)
                }
            }
        }
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
