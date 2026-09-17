package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.R

sealed class Screen(val route: String, @param:StringRes val title: Int, @param:DrawableRes val iconRes: Int) {
    object Dashboard : Screen("dashboard", R.string.tab_dashboard, R.drawable.ic_tab_dashboard)
    object Device : Screen("device", R.string.tab_device, R.drawable.ic_tab_device)
    object System : Screen("system", R.string.tab_system, R.drawable.ic_tab_system)
    object Cpu : Screen("cpu", R.string.tab_cpu, R.drawable.ic_tab_cpu)
    object Location : Screen("location", R.string.tab_location, R.drawable.ic_tab_location)
    object Network : Screen("network", R.string.tab_network, R.drawable.ic_tab_network)
    object Storage : Screen("storage", R.string.tab_storage, R.drawable.ic_tab_storage)
    object Battery : Screen("battery", R.string.tab_battery, R.drawable.ic_tab_battery)
    object Display : Screen("display", R.string.tab_display, R.drawable.ic_tab_display)
    object Sensors : Screen("sensors", R.string.tab_sensors, R.drawable.ic_tab_sensors)
    object Apps : Screen("apps", R.string.tab_apps, R.drawable.ic_tab_apps)
    object Camera : Screen("camera", R.string.tab_camera, R.drawable.ic_tab_camera)
    object Settings : Screen("settings", R.string.settings_title, R.drawable.ic_tab_settings)
}
