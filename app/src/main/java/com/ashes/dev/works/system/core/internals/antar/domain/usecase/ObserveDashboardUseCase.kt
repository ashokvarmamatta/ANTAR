package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.getOrNull
import com.ashes.dev.works.system.core.internals.antar.domain.model.DashboardSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Builds the Dashboard from the feature use cases. Static facts (device, OS, CPU, sensors) are read
 * once per collection; battery and uptime drive live updates, and RAM/storage are re-read on each
 * of those ticks. The installed-app count is only read after the Apps disclosure was accepted.
 */
class ObserveDashboardUseCase(
    private val getDeviceInfo: GetDeviceInfoUseCase,
    private val getSystemInfo: GetSystemInfoUseCase,
    private val getCpuInfo: GetCpuInfoUseCase,
    private val getSensors: GetSensorsUseCase,
    private val getStorageInfo: GetStorageInfoUseCase,
    private val getInstalledAppCount: GetInstalledAppCountUseCase,
    private val observeBattery: ObserveBatteryUseCase,
    private val observeUptime: ObserveUptimeUseCase,
    private val observeSettings: ObserveSettingsUseCase
) {
    operator fun invoke(): Flow<DashboardSummary> = flow {
        val device = getDeviceInfo().getOrNull()
        val system = getSystemInfo().getOrNull()
        val cpu = getCpuInfo().getOrNull()
        val sensorCount = getSensors().getOrNull()?.size

        val appCount = observeSettings()
            .map { it.appsConsentGiven }
            .distinctUntilChanged()
            .map { consented -> if (consented) getInstalledAppCount().getOrNull() else null }

        emitAll(
            combine(observeBattery(), observeUptime(), appCount) { battery, uptime, apps ->
                val storage = getStorageInfo().getOrNull()
                DashboardSummary(
                    deviceName = device?.deviceName,
                    androidVersion = system?.androidVersion,
                    ram = storage?.ram,
                    internalStorage = storage?.internalStorage,
                    battery = battery.getOrNull(),
                    socName = cpu?.socName ?: cpu?.hardware,
                    coreCount = cpu?.coreCount,
                    cpuFrequencyKhz = cpu?.currentFrequencyKhz,
                    sensorCount = sensorCount,
                    appCount = apps,
                    uptimeMillis = uptime
                )
            }.conflate()
        )
    }
}
