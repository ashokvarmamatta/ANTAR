package com.ashes.dev.works.system.core.internals.antar.presentation.system.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import kotlinx.coroutines.flow.StateFlow

/** Collects the ticking uptime here so only this row recomposes every second. */
@Composable
internal fun UptimeRow(liveUptimeMillis: StateFlow<Long?>, fallbackMillis: Long) {
    val live by liveUptimeMillis.collectAsStateWithLifecycle()
    val totalSeconds = (live ?: fallbackMillis) / MILLIS_PER_SECOND
    InfoRow(
        R.string.system_label_uptime,
        stringResource(
            R.string.system_value_uptime,
            totalSeconds / SECONDS_PER_DAY,
            (totalSeconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR,
            (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE,
            totalSeconds % SECONDS_PER_MINUTE
        )
    )
}

private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3_600L
private const val SECONDS_PER_DAY = 86_400L
