package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceType
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Device facts. Everything here is a cheap in-memory read, and several values (device name,
 * operator, data state, USB debugging) can change while the app runs, so nothing is cached.
 */
class DeviceRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : DeviceRepository {

    override suspend fun getDeviceInfo(): AppResult<DeviceInfo> = withContext(io) {
        appResultOf {
            val telephony = telephonyManagerOrNull()

            DeviceInfo(
                deviceName = friendlyDeviceName() ?: Build.MODEL,
                model = Build.MODEL,
                manufacturer = Build.MANUFACTURER,
                device = Build.DEVICE,
                board = Build.BOARD,
                hardware = Build.HARDWARE,
                brand = Build.BRAND,
                androidId = readAndroidId(),
                hardwareSerial = legacySerial(),
                isHardwareSerialRestricted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O,
                buildFingerprint = Build.FINGERPRINT,
                deviceType = if (context.resources.configuration.smallestScreenWidthDp >= TABLET_MIN_WIDTH_DP) {
                    DeviceType.TABLET
                } else {
                    DeviceType.PHONE
                },
                networkOperator = telephony?.let { tm ->
                    runCatching { tm.networkOperatorName }.getOrNull()?.takeIf { it.isNotBlank() }
                },
                // dataNetworkType needs READ_PHONE_STATE on API 30+; the data state is unprivileged.
                isMobileDataConnected = telephony?.let { tm ->
                    runCatching { tm.dataState == TelephonyManager.DATA_CONNECTED }.getOrNull()
                },
                isUsbDebuggingEnabled = runCatching {
                    Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
                }.getOrNull()
            )
        }
    }

    private fun friendlyDeviceName(): String? {
        val globalName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            runCatching { Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME) }.getOrNull()
        } else {
            null
        }
        return globalName?.takeIf { it.isNotBlank() }
            ?: runCatching { Settings.Secure.getString(context.contentResolver, BLUETOOTH_NAME_SETTING) }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
    }

    @SuppressLint("HardwareIds")
    private fun readAndroidId(): String? = runCatching {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }.getOrNull()?.takeIf { it.isNotBlank() }

    /**
     * Build.SERIAL was deprecated in API 26 and Build.getSerial() needs READ_PHONE_STATE, which ANTAR
     * does not hold, so only the legacy value below Android 8.0 is exposed.
     */
    private fun legacySerial(): String? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            Build.SERIAL?.takeIf { it.isNotBlank() }
        } else {
            null
        }

    private fun telephonyManagerOrNull(): TelephonyManager? {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) return null
        return context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    }

    private companion object {
        const val TABLET_MIN_WIDTH_DP = 600
        const val BLUETOOTH_NAME_SETTING = "bluetooth_name"
    }
}
