package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon
import com.ashes.dev.works.system.core.internals.antar.presentation.network.labelRes

@Composable
internal fun MobileDataCard(telephony: TelephonyInfo, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_mobile_data, icon = Icons.Outlined.CellTower, accentColor = AntarPurple)
        InfoRow(
            R.string.network_label_status,
            telephony.isMobileDataConnected?.let { connected ->
                stringResource(if (connected) R.string.network_data_connected else R.string.network_data_disconnected)
            }
        )
        InfoRow(R.string.network_label_sim_slots, telephony.modemCount.toString())
        InfoRow(R.string.network_label_phone_type, stringResource(telephony.phoneType.labelRes()))
    }
}
