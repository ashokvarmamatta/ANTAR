package com.ashes.dev.works.system.core.internals.antar.presentation.battery

import android.text.format.DateFormat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryHealth
import com.ashes.dev.works.system.core.internals.antar.domain.model.ChargerType
import java.text.SimpleDateFormat
import java.util.Date

@StringRes
internal fun BatteryHealth.labelRes(): Int = when (this) {
    BatteryHealth.GOOD -> R.string.battery_health_good
    BatteryHealth.OVERHEAT -> R.string.battery_health_overheat
    BatteryHealth.DEAD -> R.string.battery_health_dead
    BatteryHealth.OVER_VOLTAGE -> R.string.battery_health_over_voltage
    BatteryHealth.UNSPECIFIED_FAILURE -> R.string.battery_health_unspecified_failure
    BatteryHealth.COLD -> R.string.battery_health_cold
    BatteryHealth.UNKNOWN -> R.string.common_unknown
}

@StringRes
internal fun ChargerType.labelRes(): Int = when (this) {
    ChargerType.AC -> R.string.battery_charger_ac
    ChargerType.USB -> R.string.battery_charger_usb
    ChargerType.WIRELESS -> R.string.battery_charger_wireless
    ChargerType.DOCK -> R.string.battery_charger_dock
    ChargerType.NONE -> R.string.battery_charger_none
}

@StringRes
internal fun BatteryMetric.labelRes(): Int = when (this) {
    BatteryMetric.CURRENT -> R.string.battery_metric_current
    BatteryMetric.POWER -> R.string.battery_metric_power
    BatteryMetric.TEMPERATURE -> R.string.battery_metric_temperature
}

@StringRes
internal fun HistoryRange.labelRes(): Int = when (this) {
    HistoryRange.HOURS_24 -> R.string.battery_range_24h
    HistoryRange.DAYS_7 -> R.string.battery_range_7d
}

/** Grade for estimated max capacity as a percentage of design capacity. */
@StringRes
internal fun capacityGradeRes(percent: Int): Int = when {
    percent > 95 -> R.string.battery_capacity_excellent
    percent > 90 -> R.string.battery_capacity_very_good
    percent > 85 -> R.string.battery_capacity_good
    else -> R.string.battery_capacity_fair
}

@Composable
internal fun pluralResource(@PluralsRes id: Int, count: Int): String =
    LocalResources.current.getQuantityString(id, count, count)

/** Formats epoch millis with a locale- and 12/24 h-aware pattern. */
internal class TimeFormatter(private val format: SimpleDateFormat) {
    fun format(epochMillis: Long): String = format.format(Date(epochMillis))
}

internal enum class TimeStyle { TIME, DAY, DAY_TIME }

/**
 * A formatter built once per locale / hour-format / style, never per row or per recomposition.
 * Uses the platform's best pattern for the locale so month names and hour cycles are localized.
 */
@Composable
internal fun rememberTimeFormatter(style: TimeStyle): TimeFormatter {
    val locale = LocalConfiguration.current.locales[0]
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    return remember(locale, is24Hour, style) {
        val skeleton = when (style) {
            TimeStyle.TIME -> if (is24Hour) "Hm" else "hma"
            TimeStyle.DAY -> "MMMd"
            TimeStyle.DAY_TIME -> if (is24Hour) "MMMdHm" else "MMMdhma"
        }
        TimeFormatter(SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, skeleton), locale))
    }
}
