package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Device : Screen("device", "Device")
    object System : Screen("system", "System")
    object Cpu : Screen("cpu", "CPU")
    object Location : Screen("location", "Location")
    object Network : Screen("network", "Network")
    object Storage : Screen("storage", "Storage")
    object Battery : Screen("battery", "Battery")
    object Display : Screen("display", "Display")
    object Sensors : Screen("sensors", "Sensors")
    object Apps : Screen("apps", "Apps")
    object Camera : Screen("camera", "Camera")
}