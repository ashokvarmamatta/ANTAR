package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
internal fun GpsDisabledBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AntarRed.copy(alpha = 0.15f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.location_gps_disabled),
            color = AntarRed,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
