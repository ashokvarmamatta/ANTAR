package com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.SensorGlyph
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.SensorTypeIcon
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.accent

@Composable
internal fun SensorsEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SensorTypeIcon(
            glyph = SensorGlyph.Node,
            accent = AntarGray,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.sensors_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
