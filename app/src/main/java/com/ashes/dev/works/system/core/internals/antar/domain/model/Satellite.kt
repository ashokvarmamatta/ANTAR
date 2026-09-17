package com.ashes.dev.works.system.core.internals.antar.domain.model

enum class GnssConstellation { GPS, GLONASS, GALILEO, BEIDOU, QZSS, IRNSS, SBAS, UNKNOWN }

data class Satellite(
    val constellation: GnssConstellation,
    val svid: Int,
    val cn0DbHz: Float,
    val elevationDegrees: Float,
    val azimuthDegrees: Float,
    /** Null below Android 8.0 or when the receiver does not report it. */
    val carrierFrequencyHz: Float?,
    val hasEphemerisData: Boolean,
    val hasAlmanacData: Boolean,
    val usedInFix: Boolean
)
