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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.LocationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationScreen(viewModel: LocationViewModel = koinViewModel()) {
    val location = viewModel.getLocation()

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
}
