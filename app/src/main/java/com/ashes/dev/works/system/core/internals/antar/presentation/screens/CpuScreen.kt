package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CpuViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CpuScreen(viewModel: CpuViewModel = koinViewModel()) {
    val cpu by viewModel.cpu.collectAsState()

    if (cpu == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AntarCyan)
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
                            imageVector = Icons.Outlined.Memory,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = AntarCyan
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = cpu!!.socName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cpu!!.cores} Cores",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AntarCyan
                            )
                            if (cpu!!.fabrication.isNotBlank() && cpu!!.fabrication != "- - -") {
                                Text(
                                    text = cpu!!.fabrication,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AntarGray
                                )
                            }
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Processor", icon = Icons.Outlined.Memory)
                    InfoRow("Cores", cpu!!.cores)
                    InfoRow("Frequency Range", cpu!!.frequencyRange)
                    InfoRow("Processor", cpu!!.processor)
                    InfoRow("Struct", cpu!!.struct)
                    InfoRow("Frequency", cpu!!.frequency)
                    InfoRow("Fabrication", cpu!!.fabrication)
                    InfoRow("Supported ABIs", cpu!!.supportedAbis)
                    InfoRow("CPU hardware", cpu!!.cpuHardware)
                    InfoRow("CPU governor", cpu!!.cpuGovernor)
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Instruction Set", icon = Icons.Outlined.Tune, accentColor = AntarBlue)
                    InfoRow("Features", cpu!!.features, singleLine = false)
                }
            }

            items(groupedCores.entries.toList(), key = { it.key.toString() }) { (_, coreInfoList) ->
                PremiumCard {
                    SectionTitle(
                        title = "Processor ${coreInfoList.joinToString(", ") { it["processor"] ?: "" }}",
                        accentColor = AntarPurple
                    )
                    coreInfoList.first().forEach { (key, value) ->
                        if (key != "Features" && key != "BogoMIPS" && key != "processor") {
                            InfoRow(key, value)
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Graphics", icon = Icons.Outlined.DeveloperBoard, accentColor = AntarGreen)
                    InfoRow("GPU renderer", cpu!!.gpuRenderer)
                    InfoRow("GPU vendor", cpu!!.gpuVendor)
                    InfoRow("OpenGL ES", cpu!!.openGlEs)
                    if (cpu!!.openGlExtensions.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .height(200.dp)
                                .nestedScroll(nestedScrollConnection)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = cpu!!.openGlExtensions,
                                style = MaterialTheme.typography.bodySmall,
                                color = AntarGray
                            )
                        }
                    }
                    InfoRow("Vulkan", cpu!!.vulkan)
                    InfoRow("Frequency", cpu!!.gpuFrequency)
                    InfoRow("Current frequency", cpu!!.currentGpuFrequency)
                }
            }
        }
    }
}
