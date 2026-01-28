package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(viewModel: LocationViewModel = koinViewModel()) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    if (locationPermissionsState.allPermissionsGranted) {
        val location by viewModel.getLocation().collectAsState(
            initial = Location(
                beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
                qzss = "- - -", irnss = "- - -", sbas = "- - -", latitude = "- - -",
                longitude = "- - -", altitude = "- - -", seaLevelAltitude = "- - -",
                speed = "- - -", speedAccurate = "- - -", pdop = "- - -",
                timeToFirstFix = "- - -", ehvDop = "- - -", hvAccurate = "- - -",
                numberOfSatellites = "- - -", bearing = "- - -", bearingAccurate = "- - -"
            )
        )

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                // Satellites Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Satellites",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        InfoRow("Beidou", location.beidou)
                        InfoRow("Navstar GPS(GPS)", location.navstarGps)
                        InfoRow("Galileo", location.galileo)
                        InfoRow("Glonass", location.glonass)
                        InfoRow("QZSS", location.qzss)
                        InfoRow("IRNSS", location.irnss)
                        InfoRow("SBAS", location.sbas)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                // Position Details Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Position Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        InfoRow("Latitude", location.latitude)
                        InfoRow("Longitude", location.longitude)
                        InfoRow("Altitude", location.altitude)
                        InfoRow("Sea level altitude", location.seaLevelAltitude)
                        InfoRow("Speed", location.speed)
                        InfoRow("Speed accurate", location.speedAccurate)
                        InfoRow("PDOP", location.pdop)
                        InfoRow("Time to first fix (TTFF)", location.timeToFirstFix)
                        InfoRow("E H/V DOP", location.ehvDop)
                        InfoRow("H/V Accurate", location.hvAccurate)
                        InfoRow("Number of satellites", location.numberOfSatellites)
                        InfoRow("Bearing", location.bearing)
                        InfoRow("Bearing accurate", location.bearingAccurate)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Location permission is required to use this feature.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }
}
