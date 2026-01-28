package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.LocationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationScreen(viewModel: LocationViewModel = koinViewModel()) {
    val location by viewModel.getLocation().collectAsState(initial = null)

    location?.let {
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
                        InfoRow("Beidou", it.beidou)
                        InfoRow("Navstar GPS(GPS)", it.navstarGps)
                        InfoRow("Galileo", it.galileo)
                        InfoRow("Glonass", it.glonass)
                        InfoRow("QZSS", it.qzss)
                        InfoRow("IRNSS", it.irnss)
                        InfoRow("SBAS", it.sbas)
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
                        InfoRow("Latitude", it.latitude)
                        InfoRow("Longitude", it.longitude)
                        InfoRow("Altitude", it.altitude)
                        InfoRow("Sea level altitude", it.seaLevelAltitude)
                        InfoRow("Speed", it.speed)
                        InfoRow("Speed accurate", it.speedAccurate)
                        InfoRow("PDOP", it.pdop)
                        InfoRow("Time to first fix (TTFF)", it.timeToFirstFix)
                        InfoRow("E H/V DOP", it.ehvDop)
                        InfoRow("H/V Accurate", it.hvAccurate)
                        InfoRow("Number of satellites", it.numberOfSatellites)
                        InfoRow("Bearing", it.bearing)
                        InfoRow("Bearing accurate", it.bearingAccurate)
                    }
                }
            }
        }
    }
}
