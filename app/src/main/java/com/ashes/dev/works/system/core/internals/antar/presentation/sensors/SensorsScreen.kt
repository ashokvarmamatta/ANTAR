package com.ashes.dev.works.system.core.internals.antar.presentation.sensors

import android.annotation.SuppressLint
import android.hardware.Sensor
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.ui.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.LabelValue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.core.ui.SensorGlyph
import com.ashes.dev.works.system.core.internals.antar.core.ui.SensorTypeIcon
import com.ashes.dev.works.system.core.internals.antar.core.ui.accent
import com.ashes.dev.works.system.core.internals.antar.core.ui.sensorGlyphFor
import com.ashes.dev.works.system.core.internals.antar.domain.model.SensorInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun SensorsScreen(viewModel: SensorsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "sensorsState"
    ) { state ->
        when (state) {
            SensorsUiState.Loading -> LoadingSkeleton()
            is SensorsUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            SensorsUiState.Empty -> SensorsEmpty()
            is SensorsUiState.Content -> SensorsContent(state.sensors)
        }
    }
}

@Composable
private fun SensorsContent(sensors: List<SensorInfo>) {
    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SensorTypeIcon(
                        glyph = SensorGlyph.Node,
                        accent = AntarGreen,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.sensors_header_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pluralStringResource(R.plurals.sensors_count, sensors.size, sensors.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        itemsIndexed(sensors, key = { _, sensor -> sensor.id }) { index, sensor ->
            SensorItem(
                sensor = sensor,
                modifier = Modifier
                    .animateItem()
                    .staggeredEntry(index + 1)
            )
        }
    }
}

@Composable
private fun SensorItem(sensor: SensorInfo, modifier: Modifier = Modifier) {
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
private fun SensorsEmpty() {
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

@Composable
private fun sensorTypeName(type: Int): String =
    sensorTypeNameRes(type)?.let { stringResource(it) } ?: stringResource(R.string.sensors_type_other, type)

/** Name for a platform sensor type; null for vendor-defined or future types. */
@StringRes
@SuppressLint("InlinedApi") // Compile-time constants: safe to reference below their API level.
@Suppress("DEPRECATION") // TYPE_ORIENTATION and TYPE_TEMPERATURE are still reported by old devices.
private fun sensorTypeNameRes(type: Int): Int? = when (type) {
    Sensor.TYPE_ACCELEROMETER -> R.string.sensors_type_accelerometer
    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> R.string.sensors_type_accelerometer_uncalibrated
    Sensor.TYPE_ACCELEROMETER_LIMITED_AXES -> R.string.sensors_type_accelerometer_limited_axes
    Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED -> R.string.sensors_type_accelerometer_limited_axes_uncalibrated
    Sensor.TYPE_AMBIENT_TEMPERATURE -> R.string.sensors_type_ambient_temperature
    Sensor.TYPE_GAME_ROTATION_VECTOR -> R.string.sensors_type_game_rotation_vector
    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> R.string.sensors_type_geomagnetic_rotation_vector
    Sensor.TYPE_GRAVITY -> R.string.sensors_type_gravity
    Sensor.TYPE_GYROSCOPE -> R.string.sensors_type_gyroscope
    Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> R.string.sensors_type_gyroscope_uncalibrated
    Sensor.TYPE_GYROSCOPE_LIMITED_AXES -> R.string.sensors_type_gyroscope_limited_axes
    Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED -> R.string.sensors_type_gyroscope_limited_axes_uncalibrated
    Sensor.TYPE_HEADING -> R.string.sensors_type_heading
    Sensor.TYPE_HEAD_TRACKER -> R.string.sensors_type_head_tracker
    Sensor.TYPE_HEART_BEAT -> R.string.sensors_type_heart_beat
    Sensor.TYPE_HEART_RATE -> R.string.sensors_type_heart_rate
    Sensor.TYPE_HINGE_ANGLE -> R.string.sensors_type_hinge_angle
    Sensor.TYPE_LIGHT -> R.string.sensors_type_light
    Sensor.TYPE_LINEAR_ACCELERATION -> R.string.sensors_type_linear_acceleration
    Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> R.string.sensors_type_low_latency_offbody_detect
    Sensor.TYPE_MAGNETIC_FIELD -> R.string.sensors_type_magnetic_field
    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> R.string.sensors_type_magnetic_field_uncalibrated
    Sensor.TYPE_MOTION_DETECT -> R.string.sensors_type_motion_detect
    Sensor.TYPE_ORIENTATION -> R.string.sensors_type_orientation
    Sensor.TYPE_POSE_6DOF -> R.string.sensors_type_pose_6dof
    Sensor.TYPE_PRESSURE -> R.string.sensors_type_pressure
    Sensor.TYPE_PROXIMITY -> R.string.sensors_type_proximity
    Sensor.TYPE_RELATIVE_HUMIDITY -> R.string.sensors_type_relative_humidity
    Sensor.TYPE_ROTATION_VECTOR -> R.string.sensors_type_rotation_vector
    Sensor.TYPE_SIGNIFICANT_MOTION -> R.string.sensors_type_significant_motion
    Sensor.TYPE_STATIONARY_DETECT -> R.string.sensors_type_stationary_detect
    Sensor.TYPE_STEP_COUNTER -> R.string.sensors_type_step_counter
    Sensor.TYPE_STEP_DETECTOR -> R.string.sensors_type_step_detector
    Sensor.TYPE_TEMPERATURE -> R.string.sensors_type_temperature
    else -> null
}
