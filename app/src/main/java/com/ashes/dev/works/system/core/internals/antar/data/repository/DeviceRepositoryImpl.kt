package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Device
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class DeviceRepositoryImpl(private val context: Context) : DeviceRepository {

    private var cachedDevice: Device? = null
    private val _deviceFlow = MutableStateFlow<Device?>(null)

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
            googleAdvertisingId = "- - -",
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
        _deviceFlow.value = device
        fetchAdvertisingId()
        return device
    }

    private fun get6GSupport(): String {
        return "No"
    }

    private fun fetchAdvertisingId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                val id = if (!adInfo.isLimitAdTrackingEnabled) {
                    adInfo.id ?: "Not available"
                } else {
                    "Limited by user"
                }
                cachedDevice = cachedDevice?.copy(googleAdvertisingId = id)
                _deviceFlow.value = cachedDevice
            } catch (e: Exception) {
                cachedDevice = cachedDevice?.copy(googleAdvertisingId = "Unavailable")
                _deviceFlow.value = cachedDevice
            }
        }
    }


    @SuppressLint("HardwareIds")
    private fun getHardwareSerial(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            Build.SERIAL ?: "Unknown"
        } else {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // Note: On Android 10+, this will likely return "Unknown" or throw an exception due to privacy restrictions
                    // for non-system apps.
                    Build.getSerial() ?: "Unknown"
                } else {
                    "Permission Required"
                }
            } catch (e: SecurityException) {
                "Permission Denied"
            } catch (e: Exception) {
                "Unknown"
            }
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
        // Initial emit
        emit(getDevice())
        // Observe updates (like Advertising ID)
        _deviceFlow.collect { device ->
            device?.let { emit(it) }
        }
    }

    private fun getNetworkType(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
