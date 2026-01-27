package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Apps
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.model.Camera
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.domain.model.Dashboard
import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.model.Display
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.model.Network
import com.ashes.dev.works.system.core.internals.antar.domain.model.Sensors
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.domain.model.System

interface DeviceRepository {
    fun getDevice(): Device
    fun getSystem(): System
    fun getCpu(): Cpu
    fun getLocation(): Location
    fun getNetwork(): Network
    fun getStorage(): Storage
    fun getBattery(): Battery
    fun getDisplay(): Display
    fun getSensors(): Sensors
    fun getApps(): Apps
    fun getCamera(): Camera
    fun getDashboard(): Dashboard
}