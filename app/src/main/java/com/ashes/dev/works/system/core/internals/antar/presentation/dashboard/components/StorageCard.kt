package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPink
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatBytes
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatPercent
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientProgressBar
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard

// ── Storage Card ─────────────────────────────────────────────────────

@Composable
internal fun StorageCard(storage: VolumeUsage, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = storage.usedFraction,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "storage"
    )

    PremiumCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_internal_storage),
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarPurple,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_percent_used, formatPercent(storage.usedFraction)),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = Icons.Outlined.Storage,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = AntarPurple.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        GradientProgressBar(progress = fraction, height = 10.dp, colors = listOf(AntarPurple, AntarPink))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.dashboard_used, formatBytes(storage.usedBytes)),
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
            Text(
                text = stringResource(R.string.dashboard_total, formatBytes(storage.totalBytes)),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
