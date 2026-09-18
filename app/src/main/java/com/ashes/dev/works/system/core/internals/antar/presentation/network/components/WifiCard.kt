package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon
import com.ashes.dev.works.system.core.internals.antar.presentation.network.labelRes

@Composable
internal fun WifiCard(wifi: WifiState, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_wifi, icon = Icons.Outlined.Wifi, accentColor = AntarCyan)
        InfoRow(
            R.string.network_label_status,
            stringResource(if (wifi.isEnabled) R.string.common_enabled else R.string.common_disabled)
        )
        val link = wifi.connection
        if (link == null) {
            InfoRow(R.string.network_label_wifi_connection, stringResource(R.string.network_wifi_not_connected))
        } else {
            InfoRow(R.string.network_label_signal, link.rssiDbm?.let { stringResource(R.string.network_value_dbm, it) })
            InfoRow(R.string.network_label_link_speed, link.linkSpeedMbps?.let { stringResource(R.string.network_value_mbps, it) })
            InfoRow(R.string.network_label_frequency, link.frequencyMhz?.let { stringResource(R.string.network_value_mhz, it) })
            InfoRow(R.string.network_label_wifi_standard, link.standard?.let { stringResource(it.labelRes()) })
        }
    }
}
