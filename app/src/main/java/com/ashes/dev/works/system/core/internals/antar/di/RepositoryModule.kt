package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.data.local.preferences.SettingsDataStoreRepository
import com.ashes.dev.works.system.core.internals.antar.data.repository.AppsRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.BatteryRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.CameraRepositoryImpl
import com.ashes.dev.works.system.core.internals.antar.data.repository.CpuRepositoryImpl
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
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SettingsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.AppIconLoader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> { SettingsDataStoreRepository(get()) }
    single<DeviceRepository> { DeviceRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<SystemRepository> { SystemRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<CpuRepository> { CpuRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<StorageRepository> { StorageRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<BatteryRepository> { BatteryRepositoryImpl(androidContext(), get(), get(IoDispatcher)) }
    single<DisplayRepository> { DisplayRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<SensorsRepository> { SensorsRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<AppsRepository> { AppsRepositoryImpl(androidContext(), get(), get(IoDispatcher)) }
    single<NetworkRepository> { NetworkRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<LocationRepository> { LocationRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single<CameraRepository> { CameraRepositoryImpl(androidContext(), get(IoDispatcher)) }
    single { AppIconLoader(androidContext(), get(IoDispatcher)) }
}
