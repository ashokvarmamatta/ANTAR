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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CpuViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CpuScreen(viewModel: CpuViewModel = koinViewModel()) {
    val cpu = viewModel.getCpu()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Header Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = cpu.socName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Processor Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Processor",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Cores", cpu.cores)
                    InfoRow("Frequency Range", cpu.frequencyRange)
                    InfoRow("Processor", cpu.processor)
                    InfoRow("Struct", cpu.struct)
                    InfoRow("Frequency", cpu.frequency)
                    InfoRow("Fabrication", cpu.fabrication)
                    InfoRow("Supported ABIs", cpu.supportedAbis)
                    InfoRow("CPU hardware", cpu.cpuHardware)
                    InfoRow("CPU governor", cpu.cpuGovernor)
                    InfoRow("/proc/cpuinfo", cpu.procCpuinfo, singleLine = false)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Graphics Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Graphics",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("GPU renderer", cpu.gpuRenderer)
                    InfoRow("GPU vendor", cpu.gpuVendor)
                    InfoRow("OpenGL ES", cpu.openGlEs)
                    InfoRow("OpenGL extensions", cpu.openGlExtensions)
                    InfoRow("Vulkan", cpu.vulkan)
                    InfoRow("Frequency", cpu.gpuFrequency)
                    InfoRow("Current frequency", cpu.currentGpuFrequency)
                }
            }
        }
    }
}
