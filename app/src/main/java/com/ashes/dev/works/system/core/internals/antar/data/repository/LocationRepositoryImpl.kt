package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.GnssStatus
import android.location.Location as AndroidLocation
import android.location.LocationListener
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.Locale

private const val GEOCODE_MIN_DISTANCE_M = 25f

class LocationRepositoryImpl(private val context: Context) : LocationRepository {

    override fun isGpsEnabled(): Flow<Boolean> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        val checkGps = {
            val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            trySend(isEnabled)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    checkGps()
                }
            }
        }

        checkGps()
        context.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getLocation(): Flow<Location> = callbackFlow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(context, Locale.getDefault())

        var currentLocation: AndroidLocation? = null
        var currentGnssStatus: GnssStatus? = null
        var pdop: String? = null
        var hdop: String? = null
        var vdop: String? = null
        var address = "- - -"
        var lastGeocoded: AndroidLocation? = null

        fun tryEmitLocation() {
            currentLocation?.let { trySend(it.toLocationModel(currentGnssStatus, pdop, hdop, vdop, address)) }
        }

        // Reverse geocoding can hit the network and block for seconds, so it never runs on the
        // callback (main) thread, and only re-runs once the position has moved meaningfully.
        fun geocodeIfMoved(location: AndroidLocation) {
            val last = lastGeocoded
            if (last != null && last.distanceTo(location) < GEOCODE_MIN_DISTANCE_M) return
            lastGeocoded = location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { results ->
                    address = results.firstOrNull()?.getAddressLine(0) ?: "- - -"
                    tryEmitLocation()
                }
            } else {
                launch(Dispatchers.IO) {
                    address = try {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            ?.firstOrNull()?.getAddressLine(0) ?: "- - -"
                    } catch (e: Exception) {
                        "- - -"
                    }
                    tryEmitLocation()
                }
            }
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: AndroidLocation) {
                currentLocation = location
                tryEmitLocation()
                geocodeIfMoved(location)
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
        currentLocation?.let { geocodeIfMoved(it) }

        // A provider missing on this device (e.g. no NETWORK_PROVIDER) throws IllegalArgumentException.
        val providers = locationManager.allProviders
        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            if (provider in providers) {
                locationManager.requestLocationUpdates(provider, 2000, 0f, locationListener)
            }
        }

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
private fun AndroidLocation.toLocationModel(gnssStatus: GnssStatus?, pdop: String?, hdop: String?, vdop: String?, address: String): Location {
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

    val totalSatellites = satellites.size
    val satellitesInFix = satellites.count { it.usedInFix }
    
    val speedAccuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasSpeedAccuracy()) "${String.format(Locale.US, "%.2f", speedAccuracyMetersPerSecond)} m/s" else "0.0 m/s"
    val bearingAccuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasBearingAccuracy()) String.format(Locale.US, "%.2f", bearingAccuracyDegrees) else "- - -"
    return Location(
        satellites = satellites,
        latitude = String.format(Locale.US, "%.6f", latitude),
        longitude = String.format(Locale.US, "%.6f", longitude),
        altitude = String.format(Locale.US, "%.2f", altitude),
        seaLevelAltitude = "- - -",
        speed = String.format(Locale.US, "%.2f", speed),
        speedAccurate = speedAccuracy,
        pdop = pdop ?: "- - -",
        timeToFirstFix = "",
        ehvDop = if(hdop != null && vdop != null) "H: $hdop, V: $vdop" else "- - -",
        hvAccurate = String.format(Locale.US, "%.2f", accuracy),
        numberOfSatellites = if (totalSatellites > 0) "$satellitesInFix / $totalSatellites" else "- - -",
        bearing = String.format(Locale.US, "%.2f", bearing),
        bearingAccurate = bearingAccuracy,
        address = address
    )
}
