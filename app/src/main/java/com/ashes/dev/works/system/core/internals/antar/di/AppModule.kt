package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.data.repository.AppsRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.BatteryRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.CameraRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.CpuRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.DashboardRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.DeviceRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.DisplayRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.LocationRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.NetworkRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.SensorsRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.StorageRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.SystemRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CameraRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DashboardRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DeviceRepository> { DeviceRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get()) }
    single<CpuRepository> { CpuRepositoryImpl(get()) }
    single<StorageRepository> { StorageRepositoryImpl(get()) }
    single<BatteryRepository> { BatteryRepositoryImpl(get()) }
    single<NetworkRepository> { NetworkRepositoryImpl(get()) }
    single<DisplayRepository> { DisplayRepositoryImpl(get()) }
    single<SensorsRepository> { SensorsRepositoryImpl(get()) }
    single<AppsRepository> { AppsRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    single<CameraRepository> { CameraRepositoryImpl() }
    single<DashboardRepository> { DashboardRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }

    viewModel { DeviceViewModel(get()) }
    viewModel { SystemViewModel(get()) }
    viewModel { CpuViewModel(get()) }
    viewModel { StorageViewModel(get()) }
    viewModel { BatteryViewModel(get()) }
    viewModel { NetworkViewModel(get()) }
    viewModel { DisplayViewModel(get()) }
    viewModel { SensorsViewModel(get()) }
    viewModel { AppsViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
}
