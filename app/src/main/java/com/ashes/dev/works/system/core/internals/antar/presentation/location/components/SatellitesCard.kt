package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.domain.model.GnssConstellation
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.location.labelRes

@Composable
internal fun SatellitesCard(
    satellites: List<Satellite>,
    precise: Boolean,
    permanentlyDenied: Boolean,
    onRequestPreciseAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.location_section_satellites, icon = Icons.Outlined.Satellite, accentColor = AntarGreen)
        if (!precise) {
            PreciseLocationPrompt(permanentlyDenied = permanentlyDenied, onRequestAccess = onRequestPreciseAccess)
        } else {
            val counts = satellites
                .filter { it.constellation != GnssConstellation.UNKNOWN }
                .groupingBy { it.constellation }
                .eachCount()
            GnssConstellation.entries
                .filter { it != GnssConstellation.UNKNOWN }
                .map { it to (counts[it] ?: 0) }
                .sortedByDescending { it.second }
                .forEach { (constellation, count) ->
                    InfoRow(constellation.labelRes(), count.toString())
                }
        }
    }
}
