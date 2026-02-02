package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CameraViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()) {
    val camera = viewModel.getCamera()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Camera Selector Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Camera Selector",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    viewModel.cameraIds.forEach { id ->
                        val isSelected = viewModel.selectedCameraId == id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.selectCamera(id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Camera ID: $id",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "Resolution: ${camera.megaPixels}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Text(
                                        text = "Selected",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
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
