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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DashboardRepositoryImpl(
    private val deviceRepository: DeviceRepository,
    private val systemRepository: SystemRepository,
    private val storageRepository: StorageRepository,
    private val sensorsRepository: SensorsRepository,
    private val appsRepository: AppsRepository,
    private val batteryRepository: BatteryRepository,
    private val cpuRepository: CpuRepository
) : DashboardRepository {

    private var cachedSensors: String? = null
    private var cachedApps: String? = null
    private var cachedCpuName: String? = null
    private var cachedCpuDetails: String? = null

    override fun getDashboardInfo(): Flow<Dashboard> {
        return combine(
            deviceRepository.getDeviceFlow(),
            batteryRepository.getBatteryInfo()
        ) { device, battery ->
            withContext(Dispatchers.IO) {
                val system = systemRepository.getSystem()
                val storage = storageRepository.getStorage()
                
                // Fetch heavy info only if not cached to speed up initial load
                if (cachedSensors == null) {
                    cachedSensors = sensorsRepository.getSensors().sensorCountMessage.replace(" available", "")
                }
                if (cachedApps == null) {
                    cachedApps = appsRepository.getApps().appCount.replace(" installed", "")
                }
                if (cachedCpuName == null) {
                    val cpu = cpuRepository.getCpu()
                    cachedCpuName = cpu.socName
                    cachedCpuDetails = "Octa-core ${cpu.frequency}"
                }

                val ramUsageParts = storage.usedTotalMemory.split(" / ")
                val usedRam = if (ramUsageParts.isNotEmpty()) ramUsageParts[0] else "- - -"
                val totalRam = if (ramUsageParts.size > 1) ramUsageParts[1] else "- - -"

                val storageUsageParts = storage.usedTotalFreeInternal.split(" / ")
                val usedStorage = if (storageUsageParts.isNotEmpty()) storageUsageParts[0] else "- - -"
                val totalStorage = if (storageUsageParts.size > 1) storageUsageParts[1] else "- - -"

                Dashboard(
                    deviceModel = device.model,
                    deviceName = device.deviceName,
                    osVersion = system.androidVersion,
                    ramUsagePercentage = storage.usagePercentageRam.replace("%", ""),
                    usedMemory = usedRam,
                    totalMemory = totalRam,
                    freeMemory = storage.freeMemory,
                    ramStatus = "Optimized",
                    internalStoragePercentage = storage.usagePercentageInternal.replace("%", ""),
                    usedStorage = usedStorage,
                    totalStorage = totalStorage,
                    batteryStatus = if (battery.isCharging) "Charging" else "Discharging",
                    batteryLevel = battery.preciseLevel.toFloat(),
                    batteryTemp = "${battery.temperature / 10f}°C",
                    batteryVoltage = "${battery.voltage} V",
                    processorName = cachedCpuName ?: "- - -",
                    processorDetails = cachedCpuDetails ?: "- - -",
                    sensorCount = cachedSensors ?: "- - -",
                    appCount = cachedApps ?: "- - -",
                    sysHealth = "Excellent",
                    uptime = system.systemUptime
                )
            }
        }.conflate().flowOn(Dispatchers.Default)
    }
}
