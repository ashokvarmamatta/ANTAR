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

            val storageUsageParts = storage.usedTotalFreeInternal.split(" / ")
            val usedStorage = if (storageUsageParts.isNotEmpty()) storageUsageParts[0] else "- - -"
            val totalStorage = if (storageUsageParts.size > 1) storageUsageParts[1] else "- - -"


            Dashboard(
                deviceModel = device.model,
                osVersion = system.androidVersion,
                ramUsagePercentage = storage.usagePercentageRam.replace("%", ""),
                usedMemory = usedRam,
                totalMemory = totalRam,
                ramStatus = "Optimized",
                internalStoragePercentage = storage.usagePercentageInternal.replace("%", ""),
                usedStorage = usedStorage,
                totalStorage = totalStorage,
                batteryStatus = if (battery.isCharging) "Charging" else "Discharging",
                batteryTemp = "${battery.temperature / 10f}°C",
                batteryVoltage = "${battery.voltage} V",
                processorName = cpu.socName,
                processorDetails = "Octa-core ${cpu.frequency}",
                sensorCount = sensors.sensorCountMessage.replace(" available", ""),
                appCount = apps.appCount.replace(" installed", ""),
                sysHealth = "Excellent",
                uptime = system.systemUptime
            )
        }
    }
}
