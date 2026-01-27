package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.data.repository.DeviceRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.AppsViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.BatteryViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CameraViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.CpuViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DeviceViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DisplayViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.LocationViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.NetworkViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SensorsViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.StorageViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SystemViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DeviceRepository> { DeviceRepositoryImpl(androidContext()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { DeviceViewModel(get()) }
    viewModel { SystemViewModel(get()) }
    viewModel { CpuViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { NetworkViewModel(get()) }
    viewModel { StorageViewModel(get()) }
    viewModel { BatteryViewModel(get()) }
    viewModel { DisplayViewModel(get()) }
    viewModel { SensorsViewModel(get()) }
    viewModel { AppsViewModel(get()) }
    viewModel { CameraViewModel(get()) }
}