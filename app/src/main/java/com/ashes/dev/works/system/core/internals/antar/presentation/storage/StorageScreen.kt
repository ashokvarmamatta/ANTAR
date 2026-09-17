package com.ashes.dev.works.system.core.internals.antar.presentation.storage

import com.ashes.dev.works.system.core.internals.antar.core.ui.formatPercent
import com.ashes.dev.works.system.core.internals.antar.core.ui.formatBytes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientProgressBar
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPink
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.ui.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
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
private fun PartitionCard(
    @StringRes title: Int,
    icon: ImageVector,
    accent: Color,
    volume: VolumeUsage,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = title, icon = icon, accentColor = accent)
        InfoRow(R.string.storage_label_file_system, volume.fileSystemType)
        InfoRow(R.string.storage_label_path, volume.path, singleLine = false)
        InfoRow(R.string.storage_label_usage, formatPercent(volume.usedFraction))
        InfoRow(R.string.storage_label_used_total_free, usedTotalFree(volume), singleLine = false)
    }
}

/** Fills from empty to [fraction] when first shown; instant on low animation intensity. */
@Composable
private fun UsageBar(fraction: Float, percentText: String, colors: List<Color>, accent: Color) {
    val intensity = LocalAnimationIntensity.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(fraction, intensity) {
        progress.animateTo(fraction.coerceIn(0f, 1f), intensity.effectsSpec())
    }

    Spacer(modifier = Modifier.height(8.dp))
    GradientProgressBar(progress = progress.value, height = 8.dp, colors = colors)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = percentText,
        style = MaterialTheme.typography.labelSmall,
        color = accent,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun usedTotalFree(volume: VolumeUsage): String = stringResource(
    R.string.storage_value_used_total_free,
    formatBytes(volume.usedBytes),
    formatBytes(volume.totalBytes),
    formatBytes(volume.freeBytes)
)
