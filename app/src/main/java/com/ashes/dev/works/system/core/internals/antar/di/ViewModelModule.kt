package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.presentation.app.AppViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.AppsViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.battery.BatteryViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.CameraViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.cpu.CpuViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.DashboardViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.device.DeviceViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.display.DisplayViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.location.LocationViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.network.NetworkViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.sensors.SensorsViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.settings.SettingsViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.storage.StorageViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.system.SystemViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::DeviceViewModel)
    viewModelOf(::SystemViewModel)
    viewModelOf(::CpuViewModel)
    viewModelOf(::BatteryViewModel)
    viewModelOf(::StorageViewModel)
    viewModelOf(::NetworkViewModel)
    viewModelOf(::LocationViewModel)
    viewModelOf(::DisplayViewModel)
    viewModelOf(::SensorsViewModel)
    viewModelOf(::AppsViewModel)
    viewModelOf(::CameraViewModel)
    viewModelOf(::SettingsViewModel)
}
