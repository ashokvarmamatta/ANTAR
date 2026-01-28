package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.GnssStatus
import android.location.Location as AndroidLocation
import android.location.LocationListener
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class LocationRepositoryImpl(private val context: Context) : LocationRepository {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getLocation(): Flow<Location> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(context, Locale.getDefault())

        var currentLocation: AndroidLocation? = null
        var currentGnssStatus: GnssStatus? = null
        var pdop: String? = null
        var hdop: String? = null
        var vdop: String? = null

        fun tryEmitLocation() {
            currentLocation?.let { trySend(it.toLocationModel(geocoder, currentGnssStatus, pdop, hdop, vdop)) }
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

        val nmeaListener = OnNmeaMessageListener { message, _ ->
            if (message.startsWith("\$GSA") || message.startsWith("\$GNGSA")) {
                val parts = message.split(",")
                if (parts.size > 15 && parts[15].isNotEmpty()) {
                    pdop = parts[15]
                }
                if (parts.size > 16 && parts[16].isNotEmpty()) {
                    hdop = parts[16]
                }
                if (parts.size > 17 && parts[17].isNotEmpty()) {
                    val vdopPart = parts[17].split("*")
                    if (vdopPart.isNotEmpty() && vdopPart[0].isNotEmpty()) {
                        vdop = vdopPart[0]
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(Location(
                satellites = emptyList(), latitude = "Denied",
                longitude = "Denied", altitude = "Denied", seaLevelAltitude = "- - -",
                speed = "Denied", speedAccurate = "- - -", pdop = "- - -",
                timeToFirstFix = "- - -", ehvDop = "- - -", hvAccurate = "- - -",
                numberOfSatellites = "- - -", bearing = "Denied", bearingAccurate = "- - -",
                address = "- - -"
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
            locationManager.addNmeaListener(context.mainExecutor, nmeaListener)
        } else {
            @Suppress("DEPRECATION")
            locationManager.registerGnssStatusCallback(gnssStatusCallback)
            @Suppress("DEPRECATION")
            locationManager.addNmeaListener(nmeaListener)
        }

        awaitClose {
            locationManager.removeUpdates(locationListener)
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
            locationManager.removeNmeaListener(nmeaListener)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun AndroidLocation.toLocationModel(geocoder: Geocoder, gnssStatus: GnssStatus?, pdop: String?, hdop: String?, vdop: String?): Location {
    val TAG = "LocationRepository"

    val satellites = gnssStatus?.let { status ->
        (0 until status.satelliteCount).map {
            Satellite(
                constellation = when (status.getConstellationType(it)) {
                    GnssStatus.CONSTELLATION_BEIDOU -> "Beidou"
                    GnssStatus.CONSTELLATION_GPS -> "Navstar GPS"
                    GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
                    GnssStatus.CONSTELLATION_GLONASS -> "Glonass"
                    GnssStatus.CONSTELLATION_QZSS -> "QZSS"
                    GnssStatus.CONSTELLATION_IRNSS -> "IRNSS"
                    GnssStatus.CONSTELLATION_SBAS -> "SBAS"
                    else -> "Unknown"
                },
                svid = status.getSvid(it),
                cn0DbHz = status.getCn0DbHz(it),
                elevationDegrees = status.getElevationDegrees(it),
                azimuthDegrees = status.getAzimuthDegrees(it),
                hasEphemerisData = status.hasEphemerisData(it),
                hasAlmanacData = status.hasAlmanacData(it),
                usedInFix = status.usedInFix(it)
            )
        }
    } ?: emptyList()

    val satellitesInFix = satellites.count { it.usedInFix }
    val speedAccuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasSpeedAccuracy()) "${speedAccuracyMetersPerSecond} m/s" else "0.0 m/s"
    val bearingAccuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasBearingAccuracy()) bearingAccuracyDegrees.toString() else "- - -"
    val address = try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        addresses?.firstOrNull()?.getAddressLine(0) ?: "- - -"
    } catch (e: Exception) {
        "- - -"
    }

    Log.d(TAG, "Latitude: $latitude, Longitude: $longitude, Altitude: $altitude")
    Log.d(TAG, "Speed: $speed, Speed Accuracy: $speedAccuracy")
    Log.d(TAG, "Bearing: $bearing, Bearing Accuracy: $bearingAccuracy")
    Log.d(TAG, "Horizontal Accuracy (hvAccurate): $accuracy")
    Log.d(TAG, "PDOP: $pdop, HDOP: $hdop, VDOP: $vdop")
    Log.d(TAG, "Number of Satellites in fix: $satellitesInFix")
    Log.d(TAG, "Address: $address")

    return Location(
        satellites = satellites,
        latitude = latitude.toString(),
        longitude = longitude.toString(),
        altitude = altitude.toString(),
        seaLevelAltitude = "- - -",
        speed = speed.toString(),
        speedAccurate = speedAccuracy,
        pdop = pdop ?: "- - -",
        timeToFirstFix = "",
        ehvDop = if(hdop != null && vdop != null) "H: $hdop, V: $vdop" else "- - -",
        hvAccurate = accuracy.toString(),
        numberOfSatellites = satellitesInFix.toString(),
        bearing = bearing.toString(),
        bearingAccurate = bearingAccuracy,
        address = address
    )
}
