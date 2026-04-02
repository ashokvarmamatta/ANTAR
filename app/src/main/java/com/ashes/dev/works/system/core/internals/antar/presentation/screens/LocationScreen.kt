package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Satellite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AntarRed.copy(alpha = 0.15f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GPS is disabled. Enable it to get location data.",
                    color = AntarRed,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    GradientHeaderCard {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = AntarRed
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                if (location.latitude != "- - -") {
                                    Text(
                                        text = "${location.latitude}, ${location.longitude}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (location.address != "- - -" && location.address != "Permission Denied") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Map,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = AntarGray
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = location.address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AntarGray,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    PremiumCard {
                        SectionTitle(title = "Satellites", icon = Icons.Outlined.Satellite, accentColor = AntarGreen)
                        val allConstellations = listOf(
                            "Navstar GPS", "Glonass", "Galileo", "Beidou", "QZSS", "IRNSS", "SBAS"
                        )
                        val satelliteCounts = location.satellites
                            .filter { it.constellation != "Unknown" }
                            .groupingBy { it.constellation }
                            .eachCount()

                        val satelliteList = allConstellations.map {
                            it to (satelliteCounts[it] ?: 0)
                        }.sortedByDescending { it.second }

                        satelliteList.forEach { (constellation, count) ->
                            InfoRow(constellation, count.toString())
                        }
                    }
                }

                item {
                    PremiumCard {
                        SectionTitle(title = "Position Details", icon = Icons.Outlined.GpsFixed)
                        InfoRow("Latitude", location.latitude)
                        InfoRow("Longitude", location.longitude)
                        InfoRow("Altitude", location.altitude)
                        InfoRow("Sea level altitude", location.seaLevelAltitude)
                        InfoRow("Speed", location.speed)
                        InfoRow("Speed accurate", location.speedAccurate)
                        InfoRow("PDOP", location.pdop)
                        InfoRow("E H/V DOP", location.ehvDop)
                        InfoRow("H/V Accurate", location.hvAccurate)
                        InfoRow("Number of satellites", location.numberOfSatellites)
                        InfoRow("Bearing", location.bearing)
                        InfoRow("Bearing accurate", location.bearingAccurate)
                    }
                }

                if (location.address != "- - -" && location.address != "Permission Denied") {
                    item {
                        PremiumCard {
                            SectionTitle(title = "Address", icon = Icons.Outlined.Map, accentColor = AntarPurple)
                            Text(
                                text = location.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = AntarGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Location permission required",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Grant access to see GPS and location data",
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
