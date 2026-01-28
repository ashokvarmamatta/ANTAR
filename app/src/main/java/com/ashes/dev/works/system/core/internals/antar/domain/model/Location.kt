package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Location(
    val satellites: List<Satellite>,
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
    val bearingAccurate: String,
    val address: String
)
