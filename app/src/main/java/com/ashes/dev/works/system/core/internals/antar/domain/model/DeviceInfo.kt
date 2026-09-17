package com.ashes.dev.works.system.core.internals.antar.domain.model

/** Form factor derived from the smallest screen width (600dp and up is a tablet). */
enum class DeviceType { PHONE, TABLET }

/** Raw device facts. Identifiers are reported by the device; the UI labels and words everything else. */
data class DeviceInfo(
    /** User-visible device name from settings, falling back to [model]. */
    val deviceName: String,
    val model: String,
    val manufacturer: String,
    /** Build.DEVICE: the industrial design codename. */
    val device: String,
    val board: String,
    val hardware: String,
    val brand: String,
    /** Null when Settings.Secure.ANDROID_ID could not be read. */
    val androidId: String?,
    /** Null when the platform withholds it (see [isHardwareSerialRestricted]) or it is blank. */
    val hardwareSerial: String?,
    /** True on Android 8.0+, where apps without READ_PHONE_STATE cannot read the serial. */
    val isHardwareSerialRestricted: Boolean,
    val buildFingerprint: String,
    val deviceType: DeviceType,
    /** Null on devices without telephony or with no registered operator. */
    val networkOperator: String?,
    /** Null on devices without telephony. */
    val isMobileDataConnected: Boolean?,
    /** Null when the ADB setting could not be read. */
    val isUsbDebuggingEnabled: Boolean?
)
