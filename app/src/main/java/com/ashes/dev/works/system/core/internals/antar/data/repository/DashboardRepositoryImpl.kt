package com.ashes.dev.works.system.core.internals.antar.data.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DashboardRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SensorsRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DashboardRepositoryImpl(
    private val deviceRepository: DeviceRepository,
    private val systemRepository: SystemRepository,
    private val storageRepository: StorageRepository,
    private val sensorsRepository: SensorsRepository,
    private val appsRepository: AppsRepository,
    private val batteryRepository: BatteryRepository,
    private val cpuRepository: CpuRepository
) : DashboardRepository {

    override fun getDashboardInfo(): Flow<Dashboard> {
        return combine(
            batteryRepository.getBatteryInfo()
        ) { (battery) ->
            val device = deviceRepository.getDevice()
            val system = systemRepository.getSystem()
            val storage = storageRepository.getStorage()
            val sensors = sensorsRepository.getSensors()
            val apps = appsRepository.getApps()
            val cpu = cpuRepository.getCpu()

            val ramUsageParts = storage.usedTotalMemory.split(" / ")
            val usedRam = if (ramUsageParts.isNotEmpty()) ramUsageParts[0] else "- - -"
            val totalRam = if (ramUsageParts.size > 1) ramUsageParts[1] else "- - -"

            Dashboard(
                deviceModel = device.model,
                osVersion = system.androidVersion,
                ramUsage = storage.usedTotalMemory,
                ramUsagePercentage = storage.usagePercentageRam,
                usedMemory = usedRam,
                totalMemory = totalRam,
                freeMemory = storage.freeMemory,
                socName = cpu.socName,
                coreFrequencies = emptyList(),
                storageAnalysisMessage = storage.usedTotalFreeInternal,
                internalStorageUsage = storage.usagePercentageInternal,
                batteryStatus = if (battery.isCharging) "Charging" else "Discharging",
                batteryVoltage = "${battery.voltage} V",
                batteryTemp = "${battery.temperature / 10f}°C",
                sensorCount = sensors.sensorCountMessage,
                appCount = apps.appCount
            )
        }
    }
}
