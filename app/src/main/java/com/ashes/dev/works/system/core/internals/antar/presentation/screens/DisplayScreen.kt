package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DisplayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DisplayScreen(viewModel: DisplayViewModel = koinViewModel()) {
    val display = viewModel.getDisplay()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GradientHeaderCard {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Monitor,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarBlue
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${display.screenWidth} x ${display.screenHeight}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = display.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AntarCyan
                        )
                        Text(
                            text = "${display.screenSize} \u2022 ${display.refreshRate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        item {
            // Quick stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatChip(label = "Refresh", value = display.refreshRate, accentColor = AntarCyan)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatChip(label = "HDR", value = display.hdr, accentColor = AntarPurple)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatChip(label = "DPI", value = display.displayDpi, accentColor = AntarBlue)
                }
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Screen", icon = Icons.Outlined.Monitor)
                InfoRow("Name", display.name)
                InfoRow("Screen height", display.screenHeight)
                InfoRow("Screen width", display.screenWidth)
                InfoRow("Screen size", display.screenSize)
                InfoRow("Physical size", display.physicalSize)
                InfoRow("Default orientation", display.defaultOrientation)
                InfoRow("Refresh rate", display.refreshRate)
                InfoRow("HDR", display.hdr)
                InfoRow("Brightness mode", display.brightnessMode)
                InfoRow("Screen timeout", display.screenTimeout)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Metrics", icon = Icons.Outlined.AspectRatio, accentColor = AntarPurple)
                InfoRow("Display bucket", display.displayBucket)
                InfoRow("Display dpi", display.displayDpi)
                InfoRow("xdpi", display.xdpi)
                InfoRow("ydpi", display.ydpi)
                InfoRow("Logical density", display.logicalDensity)
                InfoRow("Scaled density", display.scaledDensity)
                InfoRow("Font scale", display.fontScale)
            }
        }
    }
}
