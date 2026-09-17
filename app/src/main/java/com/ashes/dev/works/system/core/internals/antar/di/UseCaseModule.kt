package com.ashes.dev.works.system.core.internals.antar.di

import com.ashes.dev.works.system.core.internals.antar.domain.usecase.CompleteIntroUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCameraIdsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCameraInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCpuInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetDeviceInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetDisplayInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetInstalledAppCountUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetSensorsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetStorageInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetSystemInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GiveAppsConsentUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.LoadInstalledAppsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.LogBatteryReadingUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveBatteryHistoryUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveBatteryUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveDashboardUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveSettingsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveUptimeUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetAccentColorUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetDynamicColorsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetMotionLevelUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.SetThemeModeUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveActiveConnectionUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveWifiStateUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetTelephonyInfoUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveLocationUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveGpsEnabledUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::SetThemeModeUseCase)
    factoryOf(::SetDynamicColorsUseCase)
    factoryOf(::SetAccentColorUseCase)
    factoryOf(::SetMotionLevelUseCase)
    factoryOf(::CompleteIntroUseCase)
    factoryOf(::GiveAppsConsentUseCase)
    factoryOf(::GetDeviceInfoUseCase)
    factoryOf(::GetSystemInfoUseCase)
    factoryOf(::ObserveUptimeUseCase)
    factoryOf(::GetCpuInfoUseCase)
    factoryOf(::GetStorageInfoUseCase)
    factoryOf(::GetSensorsUseCase)
    factoryOf(::GetDisplayInfoUseCase)
    factoryOf(::ObserveBatteryUseCase)
    factoryOf(::ObserveBatteryHistoryUseCase)
    factoryOf(::LogBatteryReadingUseCase)
    factoryOf(::LoadInstalledAppsUseCase)
    factoryOf(::GetInstalledAppCountUseCase)
    factoryOf(::GetCameraIdsUseCase)
    factoryOf(::GetCameraInfoUseCase)
    factoryOf(::ObserveActiveConnectionUseCase)
    factoryOf(::ObserveWifiStateUseCase)
    factoryOf(::GetTelephonyInfoUseCase)
    factoryOf(::ObserveLocationUseCase)
    factoryOf(::ObserveGpsEnabledUseCase)
    factoryOf(::ObserveDashboardUseCase)
}
