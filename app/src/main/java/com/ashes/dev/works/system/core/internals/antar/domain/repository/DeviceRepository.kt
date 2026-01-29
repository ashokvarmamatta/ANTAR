package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDevice(): Device
    fun getDeviceFlow(): Flow<Device>
}
