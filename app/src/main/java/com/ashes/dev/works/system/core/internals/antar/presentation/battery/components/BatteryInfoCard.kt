package com.ashes.dev.works.system.core.internals.antar.presentation.battery.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.capacityGradeRes
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.labelRes
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle

@Composable
internal fun BatteryInfoCard(battery: Battery, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.battery_section_info, icon = Icons.Outlined.Info)
        InfoRow(R.string.battery_label_health, stringResource(battery.health.labelRes()))
        InfoRow(
            R.string.battery_label_capacity_health,
            battery.capacityHealthPercent?.let { percent ->
                stringResource(
                    R.string.battery_value_capacity_health,
                    stringResource(capacityGradeRes(percent)),
                    percent
                )
            }
        )
        InfoRow(
            R.string.battery_label_temperature,
            battery.temperatureDeciCelsius?.let { stringResource(R.string.battery_value_celsius, it / 10.0) }
        )
        InfoRow(R.string.battery_label_charger_type, stringResource(battery.chargerType.labelRes()))
        InfoRow(R.string.battery_label_technology, battery.technology)
        InfoRow(
            R.string.battery_label_voltage,
            battery.voltageVolts?.let { stringResource(R.string.battery_value_volts, it) }
        )
        InfoRow(
            R.string.battery_label_design_capacity,
            battery.designCapacityMah?.let { stringResource(R.string.battery_value_mah, it) }
        )
        InfoRow(
            R.string.battery_label_estimated_max_capacity,
            battery.estimatedMaxCapacityMah?.let { stringResource(R.string.battery_value_mah, it) }
        )
        InfoRow(
            R.string.battery_label_remaining_capacity,
            battery.remainingCapacityMah?.let { stringResource(R.string.battery_value_mah, it) }
        )
        InfoRow(R.string.battery_label_charge_cycles, battery.chargeCycles?.toString())
        InfoRow(
            R.string.battery_label_current,
            battery.currentMicroAmps?.let { stringResource(R.string.battery_value_milliamps, it / 1000) }
        )
        InfoRow(
            R.string.battery_label_power,
            battery.powerWatts?.let { stringResource(R.string.battery_value_watts, it) }
        )
    }
}
