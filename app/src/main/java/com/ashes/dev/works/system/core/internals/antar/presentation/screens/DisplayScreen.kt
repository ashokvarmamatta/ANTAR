package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ashes.dev.works.system.core.internals.antar.domain.model.Display
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DisplayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DisplayScreen(viewModel: DisplayViewModel = koinViewModel()) {
    val display = viewModel.getDisplay()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item(key = "header") {
            DisplayHeader(display = display)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Screen Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Screen",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Metrics Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Metrics",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
}

@Composable
private fun DisplayHeader(display: Display) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mobile_display),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF0D47A1)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "${display.screenWidth} x ${display.screenHeight} Pixels",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = display.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "${display.screenSize} | ${display.refreshRate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = display.defaultOrientation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}
