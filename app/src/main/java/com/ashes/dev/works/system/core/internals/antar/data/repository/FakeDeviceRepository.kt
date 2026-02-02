package com.ashes.dev.works.system.core.internals.antar.data.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDeviceRepository : DeviceRepository {
    override fun getDevice(): Device {
        return Device(
            deviceName = "Pixel 8 Pro",
            model = "Pixel 8 Pro",
            manufacturer = "Google",
            device = "husky",
            board = "husky",
            hardware = "husky",
            brand = "google",
            googleAdvertisingId = "- - -",
            androidDeviceId = "- - -",
            hardwareSerial = "- - -",
            buildFingerprint = "google/husky/husky:14/UD1A.230803.041/10811862:user/release-keys",
            deviceType = "- - -",
            networkOperator = "- - -",
            networkType = "- - -",
            wifiMacAddress = "- - -",
            bluetoothMacAddress = "- - -",
            usbDebugging = "Disabled",
            supports6G = "No"
        )
    }

    override fun getDeviceFlow(): Flow<Device> = flow {
        emit(getDevice())
    }
}
