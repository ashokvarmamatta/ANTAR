package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
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
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard

@Composable
internal fun LocationHeader(location: Location?, modifier: Modifier = Modifier) {
    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = AntarRed
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                if (location == null) {
                    Text(
                        text = stringResource(R.string.location_waiting_for_fix),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    FixSummary(location)
                }
            }
        }
    }
}
