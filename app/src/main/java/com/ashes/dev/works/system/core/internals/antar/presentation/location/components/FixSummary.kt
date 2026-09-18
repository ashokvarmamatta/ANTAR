package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location

@Composable
internal fun FixSummary(location: Location) {
    Text(
        text = stringResource(R.string.location_value_coordinates, location.latitude, location.longitude),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    val inView = location.satellites.size
    if (inView > 0) {
        Text(
            text = pluralStringResource(R.plurals.location_satellites_in_view_count, inView, inView),
            style = MaterialTheme.typography.bodySmall,
            color = AntarGreen
        )
    }
    location.address?.let { address ->
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = AntarGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
