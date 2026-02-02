package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val isGpsEnabled by viewModel.isGpsEnabled().collectAsState(initial = true)

    Column {
        if (!isGpsEnabled && locationPermissionsState.allPermissionsGranted) {
            Text(
                text = "Device GPS is off. Enable it to get values.",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        if (locationPermissionsState.allPermissionsGranted) {
            val location by viewModel.getLocation().collectAsState(
                initial = Location(
                    satellites = emptyList(), latitude = "- - -",
                    longitude = "- - -", altitude = "- - -", seaLevelAltitude = "- - -",
                    speed = "- - -", speedAccurate = "- - -", pdop = "- - -",
                    timeToFirstFix = "", ehvDop = "- - -", hvAccurate = "- - -",
                    numberOfSatellites = "- - -", bearing = "- - -", bearingAccurate = "- - -",
                    address = "- - -"
                )
            )

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Satellites",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn {
                                val allConstellations = listOf(
                                    "Navstar GPS",
                                    "Glonass",
                                    "Galileo",
                                    "Beidou",
                                    "QZSS",
                                    "IRNSS",
                                    "SBAS"
                                )

                                val satelliteCounts = location.satellites
                                    .filter { it.constellation != "Unknown" }
                                    .groupingBy { it.constellation }
                                    .eachCount()

                                val satelliteList = allConstellations.map {
                                    it to (satelliteCounts[it] ?: 0)
                                }.sortedByDescending { it.second }

                                items(satelliteList) { (constellation, count) ->
                                    InfoRow(constellation, count.toString())
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
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
                            Text(
                                text = "Time to first fix (TTFF)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            InfoRow("E H/V DOP", location.ehvDop)
                            InfoRow("H/V Accurate", location.hvAccurate)
                            InfoRow("Number of satellites", location.numberOfSatellites)
                            InfoRow("Bearing", location.bearing)
                            InfoRow("Bearing accurate", location.bearingAccurate)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Address",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (location.address == "Permission Denied") {
                                Text("Permission not granted to access location.")
                            } else {
                                Text(location.address)
                            }
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
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
