package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

import androidx.annotation.DrawableRes
import com.ashes.dev.works.system.core.internals.antar.R

sealed class Screen(val route: String, val title: String, @param:DrawableRes val iconRes: Int) {
    object Dashboard : Screen("dashboard", "Dashboard", R.drawable.ic_tab_dashboard)
    object Device : Screen("device", "Device", R.drawable.ic_tab_device)
    object System : Screen("system", "System", R.drawable.ic_tab_system)
    object Cpu : Screen("cpu", "CPU", R.drawable.ic_tab_cpu)
    object Location : Screen("location", "Location", R.drawable.ic_tab_location)
    object Network : Screen("network", "Network", R.drawable.ic_tab_network)
    object Storage : Screen("storage", "Storage", R.drawable.ic_tab_storage)
    object Battery : Screen("battery", "Battery", R.drawable.ic_tab_battery)
    object Display : Screen("display", "Display", R.drawable.ic_tab_display)
    object Sensors : Screen("sensors", "Sensors", R.drawable.ic_tab_sensors)
    object Apps : Screen("apps", "Apps", R.drawable.ic_tab_apps)
    object Camera : Screen("camera", "Camera", R.drawable.ic_tab_camera)
    object Settings : Screen("settings", "Settings", R.drawable.ic_tab_settings)
}
