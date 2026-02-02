package com.ashes.dev.works.system.core.internals.antar.presentation.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CpuViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CpuScreen(viewModel: CpuViewModel = koinViewModel()) {
    val cpu by viewModel.cpu.collectAsState()

    if (cpu == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val groupedCores = cpu!!.procCpuinfo.groupBy { it["CPU part"] to it["CPU revision"] }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    return if (source == NestedScrollSource.Drag) available else Offset.Zero
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item(key = "header") {
                CpuHeader(cpu = cpu!!)
            }

            item(key = "processor") { 
                Spacer(modifier = Modifier.height(16.dp))
                ProcessorCard(cpu = cpu!!)
            }

            item(key = "instruction_set") { 
                Spacer(modifier = Modifier.height(16.dp))
                InstructionSetCard(features = cpu!!.features)
            }

            items(groupedCores.entries.toList(), key = { it.key.toString() }) { (key, coreInfoList) ->
                Spacer(modifier = Modifier.height(16.dp))
                ProcessorCoreCard(coreInfoList = coreInfoList)
            }

            item(key = "graphics") { 
                Spacer(modifier = Modifier.height(16.dp))
                GraphicsCard(cpu = cpu!!, nestedScrollConnection = nestedScrollConnection)
            }
        }
    }
}

@Composable
private fun CpuHeader(cpu: Cpu) {
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
                painter = painterResource(id = R.drawable.ic_cpu_chip),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = cpu.socName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "Cores: ${cpu.cores}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                
                Text(
                    text = cpu.fabrication,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProcessorCard(cpu: Cpu) {
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
        }
    }
}

@Composable
private fun InstructionSetCard(features: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Instruction Set",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("Features", features, singleLine = false)
        }
    }
}

@Composable
private fun ProcessorCoreCard(coreInfoList: List<Map<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Processor ${coreInfoList.joinToString(", ") { it["processor"] ?: "" }}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            coreInfoList.first().forEach { (key, value) ->
                if (key != "Features" && key != "BogoMIPS" && key != "processor") {
                    InfoRow(key, value)
                }
            }
        }
    }
}

@Composable
private fun GraphicsCard(cpu: Cpu, nestedScrollConnection: NestedScrollConnection) {
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
            Column(
                modifier = Modifier
                    .height(250.dp)
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = cpu.openGlExtensions,
                    modifier = Modifier.alpha(0.7f)
                )
            }
            InfoRow("Vulkan", cpu.vulkan)
            InfoRow("Frequency", cpu.gpuFrequency)
            InfoRow("Current frequency", cpu.currentGpuFrequency)
        }
    }
}
