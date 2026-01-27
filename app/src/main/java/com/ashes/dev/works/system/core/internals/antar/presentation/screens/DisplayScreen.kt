package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DisplayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DisplayScreen(viewModel: DisplayViewModel = koinViewModel()) {
    val display = viewModel.getDisplay()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
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
