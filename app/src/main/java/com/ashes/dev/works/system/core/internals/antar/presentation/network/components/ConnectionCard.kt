package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon
import com.ashes.dev.works.system.core.internals.antar.presentation.network.labelRes

@Composable
internal fun ConnectionCard(connection: ActiveConnection, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.network_section_connection, icon = Icons.Outlined.Public)
        InfoRow(R.string.network_label_type, stringResource(connection.type.labelRes()))
        InfoRow(R.string.network_label_ip, connection.ipv4Address)
        InfoRow(
            R.string.network_label_ipv6,
            connection.ipv6Addresses.takeIf { it.isNotEmpty() }?.joinToString(separator = "\n"),
            singleLine = false
        )
        InfoRow(R.string.network_label_netmask, connection.netmask)
        InfoRow(R.string.network_label_gateway, connection.gateway)
        InfoRow(R.string.network_label_dhcp_server, connection.dhcpServer)
        InfoRow(
            R.string.network_label_dhcp_lease,
            connection.dhcpLeaseSeconds?.let { pluralStringResource(R.plurals.network_value_lease_seconds, it, it) }
        )
        connection.dnsServers.forEachIndexed { index, server ->
            InfoRow(label = stringResource(R.string.network_label_dns, index + 1), value = server)
        }
        InfoRow(R.string.network_label_interface, connection.interfaceName)
    }
}
