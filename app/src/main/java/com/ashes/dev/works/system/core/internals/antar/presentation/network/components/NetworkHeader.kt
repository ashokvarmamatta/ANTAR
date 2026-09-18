package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiConnection
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon
import com.ashes.dev.works.system.core.internals.antar.presentation.network.labelRes

@Composable
internal fun NetworkHeader(connection: ActiveConnection, wifiLink: WifiConnection?, modifier: Modifier = Modifier) {
    val typeLabel = stringResource(connection.type.labelRes())
    val frequency = wifiLink?.frequencyMhz?.let { stringResource(R.string.network_value_mhz, it) }

    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = connection.type.icon(),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = AntarBlue
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.network_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                connection.ipv4Address?.let { ip ->
                    Text(text = ip, style = MaterialTheme.typography.bodyMedium, color = AntarCyan)
                }
                Text(
                    text = if (frequency != null) {
                        stringResource(R.string.network_value_type_and_frequency, typeLabel, frequency)
                    } else {
                        typeLabel
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }
        }
    }
}
