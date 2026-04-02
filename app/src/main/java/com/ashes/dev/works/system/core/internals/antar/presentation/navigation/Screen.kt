package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.SettingsSystemDaydream
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Outlined.Dashboard)
    object Device : Screen("device", "Device", Icons.Outlined.PhoneAndroid)
    object System : Screen("system", "System", Icons.Outlined.SettingsSystemDaydream)
    object Cpu : Screen("cpu", "CPU", Icons.Outlined.Memory)
    object Location : Screen("location", "Location", Icons.Outlined.LocationOn)
    object Network : Screen("network", "Network", Icons.Outlined.Wifi)
    object Storage : Screen("storage", "Storage", Icons.Outlined.Storage)
    object Battery : Screen("battery", "Battery", Icons.Outlined.BatteryStd)
    object Display : Screen("display", "Display", Icons.Outlined.Monitor)
    object Sensors : Screen("sensors", "Sensors", Icons.Outlined.Sensors)
    object Apps : Screen("apps", "Apps", Icons.Outlined.Apps)
    object Camera : Screen("camera", "Camera", Icons.Outlined.CameraAlt)
}
