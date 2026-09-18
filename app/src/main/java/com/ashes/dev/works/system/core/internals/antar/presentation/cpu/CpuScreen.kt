package com.ashes.dev.works.system.core.internals.antar.presentation.cpu

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.cpu.components.GraphicsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun CpuScreen(viewModel: CpuViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "cpuState"
    ) { state ->
        when (state) {
            CpuUiState.Loading -> LoadingSkeleton()
            is CpuUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is CpuUiState.Content -> CpuContent(cpu = state.cpu, coreGroups = state.coreGroups)
        }
    }
}

@Composable
private fun CpuContent(cpu: CpuInfo, coreGroups: List<CpuCoreGroup>) {
    val socTitle = cpu.socName ?: cpu.hardware ?: stringResource(R.string.common_unknown)
    val coreCountText = pluralStringResource(R.plurals.cpu_core_count, cpu.coreCount, cpu.coreCount)
    val fabrication = cpu.fabricationNm?.let { stringResource(R.string.cpu_value_fabrication_nm, it) }
    val currentFrequency = cpu.currentFrequencyKhz?.let { mhzText(it / KHZ_PER_MHZ) }
    val minFrequency = cpu.minFrequencyKhz?.let { mhzText(it / KHZ_PER_MHZ) }
    val maxFrequency = cpu.maxFrequencyKhz?.let { mhzText(it / KHZ_PER_MHZ) }
    val frequencyRange = if (minFrequency != null && maxFrequency != null) {
        stringResource(R.string.cpu_value_frequency_range, minFrequency, maxFrequency)
    } else {
        null
    }

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
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
                            text = socTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = coreCountText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AntarCyan
                        )
                        fabrication?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = AntarGray
                            )
                        }
                    }
                }
            }
        }

        item(key = "processor") {
            PremiumCard(modifier = Modifier.staggeredEntry(1)) {
                SectionTitle(title = R.string.cpu_section_processor, icon = Icons.Outlined.Memory)
                InfoRow(R.string.cpu_label_cores, cpu.coreCount.toString())
                InfoRow(R.string.cpu_label_frequency_range, frequencyRange)
                InfoRow(R.string.cpu_label_processor, cpu.socName)
                InfoRow(R.string.cpu_label_struct, cpu.architecture)
                InfoRow(R.string.cpu_label_frequency, currentFrequency)
                InfoRow(R.string.cpu_label_fabrication, fabrication)
                InfoRow(R.string.cpu_label_supported_abis, cpu.supportedAbis.joinToString(LIST_SEPARATOR))
                InfoRow(R.string.cpu_label_hardware, cpu.hardware)
                InfoRow(R.string.cpu_label_governor, cpu.governor)
            }
        }

        if (cpu.features.isNotEmpty()) {
            item(key = "instructionSet") {
                PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                    SectionTitle(title = R.string.cpu_section_instruction_set, icon = Icons.Outlined.Tune, accentColor = AntarBlue)
                    InfoRow(R.string.cpu_label_features, cpu.features.joinToString(LIST_SEPARATOR), singleLine = false)
                }
            }
        }

        itemsIndexed(coreGroups, key = { _, group -> group.key }) { index, group ->
            PremiumCard(
                modifier = Modifier
                    .animateItem()
                    .staggeredEntry(index + FIRST_CORE_ENTRY_INDEX)
            ) {
                SectionTitle(
                    title = stringResource(
                        R.string.cpu_section_core_group,
                        group.processors.joinToString(LIST_SEPARATOR)
                    ),
                    accentColor = AntarPurple
                )
                // Labels are the kernel's own /proc/cpuinfo keys, shown verbatim.
                group.fields.forEach { field ->
                    InfoRow(field.key, field.value)
                }
            }
        }

        item(key = "graphics") {
            GraphicsCard(
                gpu = cpu.gpu,
                modifier = Modifier.staggeredEntry(coreGroups.size + FIRST_CORE_ENTRY_INDEX)
            )
        }
    }
}

@Composable
internal fun mhzText(mhz: Long): String = stringResource(R.string.cpu_value_frequency_mhz, mhz)

private const val KHZ_PER_MHZ = 1_000L

/** Header, processor and instruction-set cards come first. */
private const val FIRST_CORE_ENTRY_INDEX = 3

/** Joins device-reported tokens (ABIs, feature flags, core numbers). Punctuation only. */
private const val LIST_SEPARATOR = ", "
