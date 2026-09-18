package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard

// ── Battery Card ─────────────────────────────────────────────────────

@Composable
internal fun BatteryCard(battery: Battery, modifier: Modifier = Modifier) {
    val level = battery.preciseLevelPercent.toFloat()
    val batteryColor = if (battery.isCharging) AntarGreen else when {
        level > 50 -> AntarCyan
        level > 20 -> AntarOrange
        else -> AntarRed
    }
    val animatedLevel by animateFloatAsState(
        targetValue = level,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "battery"
    )
    val details = listOfNotNull(
        stringResource(if (battery.isCharging) R.string.dashboard_charging else R.string.dashboard_discharging),
        battery.temperatureDeciCelsius?.let { stringResource(R.string.dashboard_temperature, it / 10f) },
        battery.voltageVolts?.let { stringResource(R.string.dashboard_voltage, it) }
    )

    PremiumCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_power_source),
                    style = MaterialTheme.typography.labelMedium,
                    color = batteryColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_percent_charged, animatedLevel.toInt()),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = details.joinToString(stringResource(R.string.dashboard_separator)),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }

            BatteryIcon(
                isCharging = battery.isCharging,
                batteryLevel = level,
                batteryColor = batteryColor,
                modifier = Modifier.size(width = 44.dp, height = 60.dp)
            )
        }
    }
}
