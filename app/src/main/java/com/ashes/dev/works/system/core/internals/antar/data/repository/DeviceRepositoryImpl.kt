package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository

class DeviceRepositoryImpl(private val context: Context) : DeviceRepository {
    override fun getDevice(): Device {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val adbEnabled = try { Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) } catch (e: Exception) { 0 }
        
        return Device(
            deviceName = Build.MODEL,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            device = Build.DEVICE,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            brand = Build.BRAND,
            googleAdvertisingId = "- - -",
            androidDeviceId = androidId ?: "- - -",
            hardwareSerial = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) Build.SERIAL else "- - -",
            buildFingerprint = Build.FINGERPRINT,
            deviceType = if (context.resources.configuration.smallestScreenWidthDp >= 600) "Tablet" else "Phone",
            networkOperator = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName ?: "- - -",
            networkType = getNetworkType(),
            wifiMacAddress = "- - -",
            bluetoothMacAddress = "- - -",
            usbDebugging = if (adbEnabled == 1) "Enabled" else "Disabled"
        )
    }

    private fun getNetworkType(): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "Permission Required"
        }
        return try {
            @Suppress("DEPRECATION")
            val type = telephonyManager.dataNetworkType
            when (type) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                TelephonyManager.NETWORK_TYPE_NR -> "5G"
                else -> "Unknown"
            }
        } catch (e: SecurityException) {
            "Permission Denied"
        }
    }
}
