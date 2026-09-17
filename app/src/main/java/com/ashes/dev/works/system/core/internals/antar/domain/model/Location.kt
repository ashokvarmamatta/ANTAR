package com.ashes.dev.works.system.core.internals.antar.domain.model

/**
 * One position fix plus the GNSS status around it. Units are in the field names; null means the
 * platform did not report the value. Satellites and DOP values need precise location.
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double?,
    /** Altitude above mean sea level; Android 14+ only. */
    val mslAltitudeMeters: Double?,
    val speedMetersPerSecond: Float?,
    val speedAccuracyMetersPerSecond: Float?,
    val horizontalAccuracyMeters: Float?,
    val verticalAccuracyMeters: Float?,
    val bearingDegrees: Float?,
    val bearingAccuracyDegrees: Float?,
    val satellites: List<Satellite>,
    val pdop: Float?,
    val hdop: Float?,
    val vdop: Float?,
    /** Reverse-geocoded address line, a device-provided value. */
    val address: String?
)
