package com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.LabelValue
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LabelValue
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.SensorTypeIcon
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.accent
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.components.sensorGlyphFor
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.sensorTypeNameRes

@Composable
internal fun SensorItem(sensor: SensorInfo, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val unknown = stringResource(R.string.common_unknown)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val glyph = sensorGlyphFor(sensor.type)
            val accent = glyph.accent
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = accent.copy(alpha = 0.1f), shape = CircleShape)
                    .border(0.5.dp, accent.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                SensorTypeIcon(
                    glyph = glyph,
                    accent = accent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name ?: unknown,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = sensorTypeName(sensor.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabelValue(R.string.sensors_label_vendor, sensor.vendor ?: unknown)
                    LabelValue(
                        R.string.sensors_label_power,
                        stringResource(R.string.sensors_value_power, sensor.powerMilliAmps)
                    )
                }
            }
        }
    }
}

@Composable
private fun sensorTypeName(type: Int): String =
    sensorTypeNameRes(type)?.let { stringResource(it) } ?: stringResource(R.string.sensors_type_other, type)
