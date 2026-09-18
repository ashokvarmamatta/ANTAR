package com.ashes.dev.works.system.core.internals.antar.presentation.storage.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatPercent
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.usedTotalFree

@Composable
internal fun PartitionCard(
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
