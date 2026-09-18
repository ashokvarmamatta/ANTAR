package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarTeal
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiConnection
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon
import com.ashes.dev.works.system.core.internals.antar.presentation.network.labelRes

@Composable
internal fun WifiIdentityCard(
    link: WifiConnection,
    accessGranted: Boolean,
    permanentlyDenied: Boolean,
    onRequestAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_wifi_identity, icon = Icons.Outlined.Security, accentColor = AntarTeal)
        if (accessGranted) {
            InfoRow(R.string.network_label_ssid, link.ssid)
            InfoRow(R.string.network_label_bssid, link.bssid)
            InfoRow(R.string.network_label_security, link.security?.let { stringResource(it.labelRes()) })
            if (link.ssid == null && link.bssid == null) {
                Text(
                    text = stringResource(R.string.network_wifi_access_location_off),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            WifiAccessPrompt(permanentlyDenied = permanentlyDenied, onRequestAccess = onRequestAccess)
        }
    }
}
