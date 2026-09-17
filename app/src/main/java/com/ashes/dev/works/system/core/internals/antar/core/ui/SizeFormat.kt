package com.ashes.dev.works.system.core.internals.antar.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R

/** Human-readable size in binary units (1 KB = 1024 B), with localized unit labels. */
@Composable
fun formatBytes(bytes: Long): String {
    if (bytes < BYTES_PER_UNIT) {
        return stringResource(R.string.storage_value_bytes, bytes.coerceAtLeast(0L))
    }
    var value = bytes / BYTES_PER_UNIT.toDouble()
    var unit = 0
    while (value >= BYTES_PER_UNIT && unit < UNIT_LABELS.lastIndex) {
        value /= BYTES_PER_UNIT
        unit++
    }
    return stringResource(UNIT_LABELS[unit], value)
}

/** Whole percent (0-100) of a 0..1 fraction, formatted with a localized percent string. */
@Composable
fun formatPercent(fraction: Float): String =
    stringResource(R.string.storage_value_percent, (fraction.coerceIn(0f, 1f) * PERCENT).toInt())

private const val BYTES_PER_UNIT = 1024L
private const val PERCENT = 100f

/** String resources for KB, MB, GB and TB, in that order. */
private val UNIT_LABELS = listOf(
    R.string.storage_value_kb,
    R.string.storage_value_mb,
    R.string.storage_value_gb,
    R.string.storage_value_tb
)
