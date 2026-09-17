package com.ashes.dev.works.system.core.internals.antar.presentation.location

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.GnssConstellation

@StringRes
internal fun GnssConstellation.labelRes(): Int = when (this) {
    GnssConstellation.GPS -> R.string.location_constellation_gps
    GnssConstellation.GLONASS -> R.string.location_constellation_glonass
    GnssConstellation.GALILEO -> R.string.location_constellation_galileo
    GnssConstellation.BEIDOU -> R.string.location_constellation_beidou
    GnssConstellation.QZSS -> R.string.location_constellation_qzss
    GnssConstellation.IRNSS -> R.string.location_constellation_irnss
    GnssConstellation.SBAS -> R.string.location_constellation_sbas
    GnssConstellation.UNKNOWN -> R.string.common_unknown
}
