package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle

@Composable
internal fun PositionCard(location: Location, modifier: Modifier = Modifier) {
    val horizontal = location.horizontalAccuracyMeters?.let { stringResource(R.string.location_value_meters, it) }
    val vertical = location.verticalAccuracyMeters?.let { stringResource(R.string.location_value_meters, it) }
    val hdop = location.hdop?.let { stringResource(R.string.location_value_dop, it) }
    val vdop = location.vdop?.let { stringResource(R.string.location_value_dop, it) }
    val satellitesInView = location.satellites.size

    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.location_section_position, icon = Icons.Outlined.GpsFixed)
        InfoRow(R.string.location_label_latitude, stringResource(R.string.location_value_coordinate, location.latitude))
        InfoRow(R.string.location_label_longitude, stringResource(R.string.location_value_coordinate, location.longitude))
        InfoRow(R.string.location_label_altitude, location.altitudeMeters?.let { stringResource(R.string.location_value_meters, it) })
        InfoRow(R.string.location_label_sea_level_altitude, location.mslAltitudeMeters?.let { stringResource(R.string.location_value_meters, it) })
        InfoRow(R.string.location_label_speed, location.speedMetersPerSecond?.let { stringResource(R.string.location_value_speed, it) })
        InfoRow(
            R.string.location_label_speed_accuracy,
            location.speedAccuracyMetersPerSecond?.let { stringResource(R.string.location_value_speed, it) }
        )
        InfoRow(R.string.location_label_pdop, location.pdop?.let { stringResource(R.string.location_value_dop, it) })
        InfoRow(
            R.string.location_label_hv_dop,
            if (hdop != null && vdop != null) stringResource(R.string.location_value_horizontal_vertical, hdop, vdop) else null
        )
        InfoRow(
            R.string.location_label_hv_accuracy,
            when {
                horizontal != null && vertical != null ->
                    stringResource(R.string.location_value_horizontal_vertical, horizontal, vertical)
                else -> horizontal
            }
        )
        InfoRow(
            R.string.location_label_satellites_used,
            if (satellitesInView > 0) {
                stringResource(R.string.location_value_used_of_total, location.satellites.count { it.usedInFix }, satellitesInView)
            } else {
                null
            }
        )
        InfoRow(R.string.location_label_bearing, location.bearingDegrees?.let { stringResource(R.string.location_value_degrees, it) })
        InfoRow(
            R.string.location_label_bearing_accuracy,
            location.bearingAccuracyDegrees?.let { stringResource(R.string.location_value_degrees, it) }
        )
    }
}
