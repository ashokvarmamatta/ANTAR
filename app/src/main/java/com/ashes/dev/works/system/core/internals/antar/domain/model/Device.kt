package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Device(
    val deviceName: String,
    val model: String,
    val manufacturer: String,
    val device: String,
    val board: String,
    val hardware: String,
    val brand: String,
    val googleAdvertisingId: String,
    val androidDeviceId: String,
    val hardwareSerial: String,
    val buildFingerprint: String,
    val deviceType: String,
    val networkOperator: String,
    val networkType: String,
    val wifiMacAddress: String,
    val bluetoothMacAddress: String,
    val usbDebugging: String
)