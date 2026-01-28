package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepositoryImpl(private val context: Context) : LocationRepository {
    override fun getLocation(): Flow<Location> = flow {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            emit(Location(
                beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
                qzss = "- - -", irnss = "- - -", sbas = "- - -", latitude = "Denied",
                longitude = "Denied", altitude = "Denied", seaLevelAltitude = "- - -",
                speed = "Denied", speedAccurate = "- - -", pdop = "- - -",
                timeToFirstFix = "- - -", ehvDop = "- - -", hvAccurate = "- - -",
                numberOfSatellites = "- - -", bearing = "Denied", bearingAccurate = "- - -"
            ))
            return@flow
        }
        val lastKnownLocation = try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: 
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            null
        }

        emit(Location(
            beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
            qzss = "- - -", irnss = "- - -", sbas = "- - -",
            latitude = lastKnownLocation?.latitude?.toString() ?: "- - -",
            longitude = lastKnownLocation?.longitude?.toString() ?: "- - -",
            altitude = lastKnownLocation?.altitude?.toString() ?: "- - -",
            seaLevelAltitude = "- - -",
            speed = lastKnownLocation?.speed?.toString() ?: "- - -",
            speedAccurate = "- - -", pdop = "- - -", timeToFirstFix = "- - -",
            ehvDop = "- - -", hvAccurate = "- - -", numberOfSatellites = "- - -",
            bearing = lastKnownLocation?.bearing?.toString() ?: "- - -",
            bearingAccurate = "- - -"
        ))
    }
}
