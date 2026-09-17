package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.annotation.SuppressLint
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
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.domain.model.GnssConstellation
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.model.Satellite
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Locale

private const val GEOCODE_MIN_DISTANCE_M = 25f
private const val UPDATE_INTERVAL_MS = 2000L

class LocationRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : LocationRepository {

    override fun observeGpsEnabled(): Flow<Boolean> = callbackFlow {
        val locationManager = locationManager()

        val checkGps = {
            trySend(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    checkGps()
                }
            }
        }

        checkGps()
        // A protected system broadcast: still delivered to a not-exported receiver.
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged().flowOn(io)

    // Lint cannot see the isGranted() checks below; GPS/GNSS calls only run with precise access.
    @SuppressLint("MissingPermission")
    override fun observeLocation(): Flow<Location> = callbackFlow {
        val locationManager = locationManager()
        val hasFine = isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarse = isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!hasFine && !hasCoarse) throw SecurityException()

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
                launch(io) {
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

        // GSA sentences carry PDOP, HDOP and VDOP in fields 15-17, for any talker ($GPGSA, $GNGSA, $GLGSA...).
        val nmeaListener = OnNmeaMessageListener { message, _ ->
            if (message.length > 6 && message.startsWith('$') && message.regionMatches(3, "GSA", 0, 3)) {
                val parts = message.substringBefore('*').split(",")
                parts.getOrNull(15)?.takeIf { it.isNotEmpty() }?.let { pdop = it }
                parts.getOrNull(16)?.takeIf { it.isNotEmpty() }?.let { hdop = it }
                parts.getOrNull(17)?.takeIf { it.isNotEmpty() }?.let { vdop = it }
            }
        }

        // Approximate-only access may use the network provider; GPS, satellites and NMEA need precise.
        val providers = locationManager.allProviders
        val wantedProviders = buildList {
            if (hasFine) add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
        }.filter { it in providers }

        currentLocation = wantedProviders.firstNotNullOfOrNull { locationManager.getLastKnownLocation(it) }
        tryEmitLocation()
        currentLocation?.let { geocodeIfMoved(it) }

        // This body runs on the io dispatcher, which has no Looper: callbacks go to the main thread.
        for (provider in wantedProviders) {
            locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL_MS, 0f, locationListener, Looper.getMainLooper())
        }

        if (hasFine) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.registerGnssStatusCallback(context.mainExecutor, gnssStatusCallback)
                locationManager.addNmeaListener(context.mainExecutor, nmeaListener)
            } else {
                val mainHandler = Handler(Looper.getMainLooper())
                @Suppress("DEPRECATION")
                locationManager.registerGnssStatusCallback(gnssStatusCallback, mainHandler)
                @Suppress("DEPRECATION")
                locationManager.addNmeaListener(nmeaListener, mainHandler)
            }
        }

        awaitClose {
            locationManager.removeUpdates(locationListener)
            if (hasFine) {
                locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
                locationManager.removeNmeaListener(nmeaListener)
            }
        }
    }.flowOn(io)

    private fun locationManager(): LocationManager =
        context.getSystemService(LocationManager::class.java) ?: throw UnsupportedOperationException()

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun AndroidLocation.toLocationModel(
    gnssStatus: GnssStatus?,
    pdop: String?,
    hdop: String?,
    vdop: String?,
    address: String
): Location {
    val satellites = gnssStatus?.let { status ->
        (0 until status.satelliteCount).map { index ->
            Satellite(
                constellation = constellationOf(status.getConstellationType(index)),
                svid = status.getSvid(index),
                cn0DbHz = status.getCn0DbHz(index),
                elevationDegrees = status.getElevationDegrees(index),
                azimuthDegrees = status.getAzimuthDegrees(index),
                carrierFrequencyHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && status.hasCarrierFrequencyHz(index)) {
                    status.getCarrierFrequencyHz(index)
                } else {
                    null
                },
                hasEphemerisData = status.hasEphemerisData(index),
                hasAlmanacData = status.hasAlmanacData(index),
                usedInFix = status.usedInFix(index)
            )
        }
    }.orEmpty()

    val isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    return Location(
        latitude = latitude,
        longitude = longitude,
        altitudeMeters = if (hasAltitude()) altitude else null,
        mslAltitudeMeters = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && hasMslAltitude()) {
            mslAltitudeMeters
        } else {
            null
        },
        speedMetersPerSecond = if (hasSpeed()) speed else null,
        speedAccuracyMetersPerSecond = if (isOreo && hasSpeedAccuracy()) speedAccuracyMetersPerSecond else null,
        horizontalAccuracyMeters = if (hasAccuracy()) accuracy else null,
        verticalAccuracyMeters = if (isOreo && hasVerticalAccuracy()) verticalAccuracyMeters else null,
        bearingDegrees = if (hasBearing()) bearing else null,
        bearingAccuracyDegrees = if (isOreo && hasBearingAccuracy()) bearingAccuracyDegrees else null,
        satellites = satellites,
        pdop = pdop?.toFloatOrNull(),
        hdop = hdop?.toFloatOrNull(),
        vdop = vdop?.toFloatOrNull(),
        // The geocoding block above keeps its "- - -" placeholder; the domain model uses null instead.
        address = address.takeUnless { it == NO_VALUE }
    )
}

private fun constellationOf(type: Int): GnssConstellation = when (type) {
    GnssStatus.CONSTELLATION_GPS -> GnssConstellation.GPS
    GnssStatus.CONSTELLATION_GLONASS -> GnssConstellation.GLONASS
    GnssStatus.CONSTELLATION_GALILEO -> GnssConstellation.GALILEO
    GnssStatus.CONSTELLATION_BEIDOU -> GnssConstellation.BEIDOU
    GnssStatus.CONSTELLATION_QZSS -> GnssConstellation.QZSS
    GnssStatus.CONSTELLATION_IRNSS -> GnssConstellation.IRNSS
    GnssStatus.CONSTELLATION_SBAS -> GnssConstellation.SBAS
    else -> GnssConstellation.UNKNOWN
}
