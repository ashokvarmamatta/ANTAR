package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.Screen

@Composable
fun MainScreen(navController: NavController) {
    val screens = listOf(
        Screen.Dashboard,
        Screen.Device,
        Screen.System,
        Screen.Cpu,
        Screen.Location,
        Screen.Network,
        Screen.Storage,
        Screen.Battery,
        Screen.Display,
        Screen.Sensors,
        Screen.Apps,
        Screen.Camera
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerNavController = rememberNavController()

    Scaffold {
        innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                screens.forEachIndexed { index, screen ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            pagerNavController.navigate(screen.route) {
                                popUpTo(pagerNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        text = { Text(text = screen.title) }
                    )
                }
            }

            NavHost(navController = pagerNavController, startDestination = Screen.Dashboard.route) {
                composable(Screen.Dashboard.route) { DashboardScreen() }
                composable(Screen.Device.route) { DeviceScreen() }
                composable(Screen.System.route) { SystemScreen() }
                composable(Screen.Cpu.route) { CpuScreen() }
                composable(Screen.Location.route) { LocationScreen() }
                composable(Screen.Network.route) { NetworkScreen() }
                composable(Screen.Storage.route) { StorageScreen() }
                composable(Screen.Battery.route) { BatteryScreen() }
                composable(Screen.Display.route) { DisplayScreen() }
                composable(Screen.Sensors.route) { SensorsScreen() }
                composable(Screen.Apps.route) { AppsScreen() }
                composable(Screen.Camera.route) { CameraScreen() }
            }
        }
    }
}
