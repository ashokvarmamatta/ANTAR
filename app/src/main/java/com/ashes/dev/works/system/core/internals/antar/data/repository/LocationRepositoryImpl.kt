
package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location as AndroidLocation
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepositoryImpl(private val context: Context) : LocationRepository {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getLocation(): Flow<Location> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var currentLocation: AndroidLocation? = null
        var currentGnssStatus: GnssStatus? = null

        fun tryEmitLocation() {
            currentLocation?.let { trySend(it.toLocationModel(currentGnssStatus)) }
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: AndroidLocation) {
                currentLocation = location
                tryEmitLocation()
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        val gnssStatusCallback = object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(status: GnssStatus) {
                currentGnssStatus = status
                tryEmitLocation()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(Location(
                beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
                qzss = "- - -", irnss = "- - -", sbas = "- - -", latitude = "Denied",
                longitude = "Denied", altitude = "Denied", seaLevelAltitude = "- - -",
                speed = "Denied", speedAccurate = "- - -", pdop = "- - -",
                timeToFirstFix = "- - -", ehvDop = "- - -", hvAccurate = "- - -",
                numberOfSatellites = "- - -", bearing = "Denied", bearingAccurate = "- - -"
            ))
            close()
            return@callbackFlow
        }

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        tryEmitLocation()

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0f, locationListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.registerGnssStatusCallback(context.mainExecutor, gnssStatusCallback)
        } else {
            locationManager.registerGnssStatusCallback(gnssStatusCallback)
        }

        awaitClose {
            locationManager.removeUpdates(locationListener)
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun AndroidLocation.toLocationModel(gnssStatus: GnssStatus?): Location {
    val satCounts = gnssStatus?.let { status ->
        val counts = mutableMapOf<Int, Int>().withDefault { 0 }
        for (i in 0 until status.satelliteCount) {
            val constellation = status.getConstellationType(i)
            counts[constellation] = counts.getValue(constellation) + 1
        }
        counts
    } ?: emptyMap()

    val irnssCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        satCounts[GnssStatus.CONSTELLATION_IRNSS] ?: 0
    } else { 0 }

    return Location(
        beidou = (satCounts[GnssStatus.CONSTELLATION_BEIDOU] ?: 0).toString(),
        navstarGps = (satCounts[GnssStatus.CONSTELLATION_GPS] ?: 0).toString(),
        galileo = (satCounts[GnssStatus.CONSTELLATION_GALILEO] ?: 0).toString(),
        glonass = (satCounts[GnssStatus.CONSTELLATION_GLONASS] ?: 0).toString(),
        qzss = (satCounts[GnssStatus.CONSTELLATION_QZSS] ?: 0).toString(),
        irnss = irnssCount.toString(),
        sbas = (satCounts[GnssStatus.CONSTELLATION_SBAS] ?: 0).toString(),
        latitude = latitude.toString(),
        longitude = longitude.toString(),
        altitude = altitude.toString(),
        seaLevelAltitude = "- - -",
        speed = speed.toString(),
        speedAccurate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasSpeedAccuracy()) speedAccuracyMetersPerSecond.toString() else "- - -",
        pdop = "- - -",
        timeToFirstFix = "- - -",
        ehvDop = "- - -",
        hvAccurate = accuracy.toString(),
        numberOfSatellites = (gnssStatus?.satelliteCount ?: 0).toString(),
        bearing = bearing.toString(),
        bearingAccurate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasBearingAccuracy()) bearingAccuracyDegrees.toString() else "- - -"
    )
}
