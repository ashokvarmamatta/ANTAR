package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.domain.model.SimInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon

@Composable
internal fun SimInfoCard(sim: SimInfo, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_sim, icon = Icons.Outlined.SimCard, accentColor = AntarGreen)
        InfoRow(R.string.network_label_sim_name, sim.operatorName ?: stringResource(R.string.network_sim_detected))
        InfoRow(R.string.network_label_country_iso, sim.countryIso)
        InfoRow(R.string.network_label_mcc, sim.mcc)
        InfoRow(R.string.network_label_mnc, sim.mnc)
        InfoRow(R.string.network_label_carrier_id, sim.carrierId?.toString())
        InfoRow(R.string.network_label_carrier_name, sim.networkOperatorName)
        InfoRow(
            R.string.network_label_roaming,
            stringResource(if (sim.isRoaming) R.string.common_yes else R.string.common_no)
        )
    }
}
