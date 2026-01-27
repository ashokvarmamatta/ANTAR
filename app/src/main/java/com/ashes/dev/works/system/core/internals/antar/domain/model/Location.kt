package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Location(
    val beidou: String,
    val navstarGps: String,
    val galileo: String,
    val glonass: String,
    val qzss: String,
    val irnss: String,
    val sbas: String,
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val seaLevelAltitude: String,
    val speed: String,
    val speedAccurate: String,
    val pdop: String,
    val timeToFirstFix: String,
    val ehvDop: String,
    val hvAccurate: String,
    val numberOfSatellites: String,
    val bearing: String,
    val bearingAccurate: String
)