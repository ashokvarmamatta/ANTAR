package com.ashes.dev.works.system.core.internals.antar.presentation.storage

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPink
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatBytes
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatPercent
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.components.PartitionCard
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.components.UsageBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun StorageScreen(viewModel: StorageViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "storageState"
    ) { state ->
        when (state) {
            StorageUiState.Loading -> LoadingSkeleton()
            is StorageUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is StorageUiState.Content -> StorageContent(state.storage)
        }
    }
}

@Composable
private fun StorageContent(storage: StorageInfo) {
    val ram = storage.ram
    val internal = storage.internalStorage
    val ramPercent = formatPercent(ram.usedFraction)
    val internalPercent = formatPercent(internal.usedFraction)

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Storage,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarPurple
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.storage_header_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.storage_header_usage, ramPercent, internalPercent),
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        item(key = "ram") {
            PremiumCard(modifier = Modifier.staggeredEntry(1)) {
                SectionTitle(title = R.string.storage_section_ram, icon = Icons.Outlined.Memory)
                InfoRow(R.string.storage_label_type, storage.ramType?.token)
                InfoRow(R.string.storage_label_free_memory, formatBytes(ram.availableBytes))
                InfoRow(
                    R.string.storage_label_used_total,
                    stringResource(R.string.storage_value_used_total, formatBytes(ram.usedBytes), formatBytes(ram.totalBytes))
                )
                UsageBar(
                    fraction = ram.usedFraction,
                    percentText = ramPercent,
                    colors = listOf(AntarCyan, AntarBlue),
                    accent = AntarCyan
                )
            }
        }

        item(key = "internal") {
            PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                SectionTitle(title = R.string.storage_section_internal, icon = Icons.Outlined.Folder, accentColor = AntarPurple)
                InfoRow(R.string.storage_label_path, internal.path, singleLine = false)
                InfoRow(R.string.storage_label_used_total_free, usedTotalFree(internal), singleLine = false)
                UsageBar(
                    fraction = internal.usedFraction,
                    percentText = internalPercent,
                    colors = listOf(AntarPurple, AntarPink),
                    accent = AntarPurple
                )
            }
        }

        storage.systemPartition?.let { system ->
            item(key = "system") {
                PartitionCard(
                    title = R.string.storage_section_system,
                    icon = Icons.Outlined.Dns,
                    accent = AntarBlue,
                    volume = system,
                    modifier = Modifier.staggeredEntry(3)
                )
            }
        }

        item(key = "data") {
            PartitionCard(
                title = R.string.storage_section_data,
                icon = Icons.Outlined.Storage,
                accent = AntarGreen,
                volume = storage.dataPartition,
                modifier = Modifier.staggeredEntry(4)
            )
        }
    }
}

@Composable
internal fun usedTotalFree(volume: VolumeUsage): String = stringResource(
    R.string.storage_value_used_total_free,
    formatBytes(volume.usedBytes),
    formatBytes(volume.totalBytes),
    formatBytes(volume.freeBytes)
)
