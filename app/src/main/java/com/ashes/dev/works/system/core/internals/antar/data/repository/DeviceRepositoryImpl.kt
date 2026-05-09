package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeviceRepositoryImpl(private val context: Context) : DeviceRepository {

    private var cachedDevice: Device? = null

    override fun getDevice(): Device {
        cachedDevice?.let { return it }

        val adbEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0)
        } catch (e: Exception) {
            0
        }

        val friendlyName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
        } else {
            null
        } ?: Settings.Secure.getString(context.contentResolver, "bluetooth_name")
          ?: Build.MODEL

        val device = Device(
            deviceName = friendlyName,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            device = Build.DEVICE,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            brand = Build.BRAND,
            androidDeviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "- - -",
            hardwareSerial = getHardwareSerial(),
            buildFingerprint = Build.FINGERPRINT,
            deviceType = if (context.resources.configuration.smallestScreenWidthDp >= 600) "Tablet" else "Phone",
            networkOperator = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
                ?: "- - -",
            networkType = getNetworkType(),
            wifiMacAddress = getWifiMacAddress(),
            bluetoothMacAddress = getBluetoothMacAddress(),
            usbDebugging = if (adbEnabled == 1) "Enabled" else "Disabled",
            supports6G = get6GSupport()
        )
        cachedDevice = device
        return device
    }

    private fun get6GSupport(): String {
        return "No"
    }

    @SuppressLint("HardwareIds")
    private fun getHardwareSerial(): String {
        // Build.SERIAL was deprecated in API 26 and Build.getSerial() requires READ_PHONE_STATE
        // (Phone permission group). ANTAR no longer holds that permission, so we expose only the
        // legacy value (which itself returns "unknown" on most modern devices).
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            Build.SERIAL ?: "Unknown"
        } else {
            "Restricted by Android"
        }
    }

    @SuppressLint("HardwareIds")
    private fun getWifiMacAddress(): String {
        return "Not available"
    }

    @SuppressLint("HardwareIds")
    private fun getBluetoothMacAddress(): String {
        return "Not available"
    }

    override fun getDeviceFlow(): Flow<Device> = flow {
        emit(getDevice())
    }

    private fun getNetworkType(): String {
        // TelephonyManager.dataNetworkType requires READ_PHONE_STATE on API 30+. Without that
        // permission we fall back to the data-connection state, which is unprivileged.
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (telephonyManager.dataState == TelephonyManager.DATA_CONNECTED) "Connected" else "Disconnected"
    }
}
