package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.MediaDrm
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
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
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DeviceRepository
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.System.getProperty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.net.ssl.SSLContext

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

    override fun getSystem(): System {
        val drmInfo = getWidevineInfo()
        val uptimeMillis = SystemClock.elapsedRealtime()
        
        return System(
            androidVersion = Build.VERSION.RELEASE,
            codename = Build.VERSION.CODENAME,
            releaseDate = getAndroidReleaseDate(Build.VERSION.SDK_INT),
            versionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Build.VERSION.RELEASE_OR_CODENAME else Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT.toString(),
            buildNumber = Build.DISPLAY,
            buildTime = formatTime(Build.TIME),
            buildId = Build.ID,
            securityPatchLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "- - -",
            baseband = Build.getRadioVersion() ?: "- - -",
            language = Locale.getDefault().displayLanguage,
            timeZone = TimeZone.getDefault().id,
            rootAccess = if (isRooted()) "Yes" else "No",
            systemUptime = formatUptime(uptimeMillis),
            systemAsRoot = if (isSystemAsRoot()) "Yes" else "No",
            seamlessUpdates = if (isSeamlessUpdateSupported()) "Supported" else "Not Supported",
            dynamicPartitions = if (getSystemProperty("ro.boot.dynamic_partitions") == "Yes") "Enabled" else "Disabled",
            projectTreble = if (getSystemProperty("ro.treble.enabled") == "Yes") "Enabled" else "Disabled",
            javaRuntime = "Android Runtime",
            javaVm = (getProperty("java.vm.name") ?: "Dalvik") + " " + (getProperty("java.vm.version") ?: ""),
            javaVmStackSize = getStackSize(),
            kernelArchitecture = getProperty("os.arch") ?: "- - -",
            kernelVersion = getProperty("os.version") ?: "- - -",
            openGlEs = getOpenGlEsVersion(),
            selinux = getSelinuxStatus(),
            openSslVersion = getOpenSslVersion(),
            drmVendor = drmInfo["vendor"] ?: "- - -",
            drmVersion = drmInfo["version"] ?: "- - -",
            drmDescription = drmInfo["description"] ?: "- - -",
            drmAlgorithm = drmInfo["algorithms"] ?: "- - -",
            drmSecurityLevel = drmInfo["securityLevel"] ?: "- - -",
            drmSystemId = drmInfo["systemId"] ?: "- - -",
            drmHdcpLevel = drmInfo["hdcpLevel"] ?: "- - -",
            drmMaxHdcpLevel = drmInfo["maxHdcpLevel"] ?: "- - -",
            drmUsageReportingSupport = drmInfo["usageReportingSupport"] ?: "- - -",
            drmMaxNumberOfSessions = drmInfo["maxNumberOfSessions"] ?: "- - -",
            drmNumberOfOpenSessions = drmInfo["numberOfOpenSessions"] ?: "- - -"
        )
    }

    private fun getStackSize(): String {
        return try {
            val runtime = Runtime.getRuntime()
            formatSize(runtime.maxMemory())
        } catch (e: Exception) {
            "- - -"
        }
    }

    private fun isSystemAsRoot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("mount")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var isSar = false
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains(" / ") == true && line?.contains("rootfs") == false) {
                    isSar = true
                    break
                }
            }
            reader.close()
            process.destroy()
            isSar || getSystemProperty("ro.build.system_root_image") == "Yes"
        } catch (e: Exception) {
            false
        }
    }

    private fun isSeamlessUpdateSupported(): Boolean {
        val slot = getSystemProperty("ro.boot.slot_suffix")
        return slot.isNotBlank() || getSystemProperty("ro.build.ab_update") == "Yes"
    }

    private fun getAndroidReleaseDate(sdkInt: Int): String {
        return when (sdkInt) {
            24 -> "August 22, 2016"
            25 -> "October 4, 2016"
            26 -> "August 21, 2017"
            27 -> "December 5, 2017"
            28 -> "August 6, 2018"
            29 -> "September 3, 2019"
            30 -> "September 8, 2020"
            31 -> "October 4, 2021"
            32 -> "March 7, 2022"
            33 -> "August 15, 2022"
            34 -> "October 4, 2023"
            35 -> "September 3, 2024"
            else -> "- - -"
        }
    }

    private fun formatTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }

    private fun formatUptime(uptimeMillis: Long): String {
        val days = uptimeMillis / (24 * 60 * 60 * 1000)
        val hours = (uptimeMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (uptimeMillis % (60 * 1000)) / (60 * 1000)
        val seconds = (uptimeMillis % (60 * 1000)) / 1000
        return "${days}d ${hours}h ${minutes}m ${seconds}s"
    }

    private fun isRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) return true
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun getSystemProperty(key: String): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()?.trim()
            reader.close()
            process.destroy()
            if (result.isNullOrBlank()) "" else if (result == "1" || result == "true") "Yes" else if (result == "0" || result == "false") "No" else result
        } catch (e: Exception) {
            ""
        }
    }

    private fun getOpenGlEsVersion(): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.deviceConfigurationInfo.glEsVersion ?: "- - -"
    }

    private fun getSelinuxStatus(): String {
        return try {
            val process = Runtime.getRuntime().exec("getenforce")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()?.trim()
            reader.close()
            process.destroy()
            if (!result.isNullOrBlank()) return result
            
            val selinuxProp = getSystemProperty("ro.build.selinux")
            if (selinuxProp.isNotBlank()) return selinuxProp

            "Enforcing"
        } catch (e: Exception) {
            "Enforcing"
        }
    }

    private fun getOpenSslVersion(): String {
        return try {
            val info = SSLContext.getDefault().provider.info ?: return "- - -"
            val cleanInfo = info.replace("\n", " ").replace(Regex("\\s+"), " ").trim()
            if (cleanInfo.contains("OpenSSL")) {
                val match = Regex("OpenSSL\\s+([\\d\\.\\w]+)").find(cleanInfo)
                match?.let { "OpenSSL " + it.groupValues[1] } ?: "OpenSSL"
            } else if (cleanInfo.contains("BoringSSL")) {
                "BoringSSL"
            } else {
                if (cleanInfo.length > 40) cleanInfo.take(40) + "..." else cleanInfo
            }
        } catch (e: Exception) {
            "- - -"
        }
    }

    private fun getWidevineInfo(): Map<String, String> {
        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
        if (!MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) {
            return emptyMap()
        }
        return try {
            val mediaDrm = MediaDrm(WIDEVINE_UUID)
            val info = mutableMapOf<String, String>()
            val properties = arrayOf(
                MediaDrm.PROPERTY_VENDOR to "vendor",
                MediaDrm.PROPERTY_VERSION to "version",
                MediaDrm.PROPERTY_DESCRIPTION to "description",
                MediaDrm.PROPERTY_ALGORITHMS to "algorithms",
                "securityLevel" to "securityLevel",
                "systemId" to "systemId",
                "hdcpLevel" to "hdcpLevel",
                "maxHdcpLevel" to "maxHdcpLevel",
                "usageReportingSupport" to "usageReportingSupport",
                "maxNumberOfSessions" to "maxNumberOfSessions",
                "numberOfOpenSessions" to "numberOfOpenSessions"
            )
            for (p in properties) {
                try {
                    val value = mediaDrm.getPropertyString(p.first)
                    if (!value.isNullOrBlank()) {
                        info[p.second] = value
                    }
                } catch (e: Exception) {
                }
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaDrm.close()
            } else {
                @Suppress("DEPRECATION")
                mediaDrm.release()
            }
            info
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override fun getCpu(): Cpu {
        val cpuInfoMap = getCpuInfoMap()
        val cpuInfoStr = try { File("/proc/cpuinfo").readText() } catch (e: Exception) { "- - -" }
        
        val minFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq")
        val maxFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
        val curFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")
        val governor = try { File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").readText().trim() } catch (e: Exception) { "- - -" }

        return Cpu(
            socName = getSystemProperty("ro.board.platform").ifBlank { cpuInfoMap["Hardware"] ?: "- - -" },
            cores = Runtime.getRuntime().availableProcessors().toString(),
            frequencyRange = if (minFreq != "- - -" && maxFreq != "- - -") "$minFreq - $maxFreq" else "- - -",
            processor = cpuInfoMap["Processor"] ?: cpuInfoMap["model name"] ?: "- - -",
            struct = getProperty("os.arch") ?: "- - -",
            frequency = curFreq,
            fabrication = "- - -",
            supportedAbis = Build.SUPPORTED_ABIS.joinToString(),
            cpuHardware = getSystemProperty("ro.hardware").ifBlank { cpuInfoMap["Hardware"] ?: "- - -" },
            cpuGovernor = governor,
            procCpuinfo = cpuInfoStr,
            gpuRenderer = "- - -",
            gpuVendor = "- - -",
            openGlEs = getOpenGlEsVersion(),
            openGlExtensions = "- - -",
            vulkan = "- - -",
            gpuFrequency = "- - -",
            currentGpuFrequency = "- - -"
        )
    }

    private fun getCpuFreq(path: String): String {
        return try {
            val freq = File(path).readText().trim().toLong()
            "${freq / 1000} MHz"
        } catch (e: Exception) {
            "- - -"
        }
    }

    private fun getCpuInfoMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            File("/proc/cpuinfo").forEachLine { line ->
                if (line.contains(":")) {
                    val parts = line.split(":")
                    if (parts.size == 2) {
                        map[parts[0].trim()] = parts[1].trim()
                    }
                }
            }
        } catch (e: Exception) {}
        return map
    }

    override fun getStorage(): Storage {
        val internalStatFs = StatFs(Environment.getDataDirectory().path)
        val totalInternal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val freeInternal = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        val usedInternalVal = totalInternal - freeInternal

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val freeRam = memoryInfo.availMem
        val usedRam = totalRam - freeRam

        return Storage(
            freeMemory = formatSize(freeRam),
            usedTotalMemory = "${formatSize(usedRam)} / ${formatSize(totalRam)}",
            usagePercentageRam = "${(usedRam.toDouble() / totalRam.toDouble() * 100).toInt()}%",
            internalStoragePath = Environment.getDataDirectory().path,
            usedTotalFreeInternal = "${formatSize(usedInternalVal)} / ${formatSize(totalInternal)} / ${formatSize(freeInternal)}",
            usagePercentageInternal = "${(usedInternalVal.toDouble() / totalInternal.toDouble() * 100).toInt()}%",
            systemStorageFileSystemType = "- - -",
            systemStoragePath = "/system",
            systemStorageUsageProgress = "- - -",
            usedTotalFreeSystem = "- - -",
            internalStorageDataFileSystemType = "- - -",
            internalStorageDataPath = "/data",
            internalStorageDataUsageProgress = "- - -",
            usedTotalFreeInternalData = "- - -"
        )
    }

    private fun formatSize(size: Long): String {
        val suffix = arrayOf("B", "KB", "MB", "GB", "TB")
        var fSize = size.toDouble()
        var i = 0
        while (fSize > 1024 && i < suffix.size - 1) {
            fSize /= 1024
            i++
        }
        return String.format(Locale.getDefault(), "%.2f %s", fSize, suffix[i])
    }

    override fun getBattery(): Battery {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level / scale.toFloat()

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        val temperature = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val technology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "- - -"

        return Battery(
            batteryLevel = "${(batteryPct * 100).toInt()}%",
            status = if (isCharging) "Charging" else "Discharging",
            current = "- - -",
            power = "- - -",
            temperature = "${temperature / 10f}°C",
            health = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                else -> "Unknown"
            },
            powerSource = getPowerSource(batteryIntent),
            technology = technology,
            voltage = "${voltage / 1000f}V",
            capacity = "- - -",
            dualCellDevice = "- - -"
        )
    }

    private fun getPowerSource(intent: Intent?): String {
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Battery"
        }
    }
    
    override fun getNetwork(): Network {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        val activeNetwork = connectivityManager.activeNetworkInfo
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return Network(
            connectionType = activeNetwork?.typeName ?: "- - -",
            statusDescription = activeNetwork?.state?.name ?: "- - -",
            signalStrength = wifiInfo.rssi.toString(),
            wifiStatus = if (wifiManager.isWifiEnabled) "Enabled" else "Disabled",
            wifiSafety = "- - -",
            bssid = wifiInfo.bssid ?: "- - -",
            dhcp = "- - -",
            dhcpLeaseDuration = "- - -",
            gateway = "- - -",
            netmask = "- - -",
            dns1 = "- - -",
            dns2 = "- - -",
            ip = "- - -",
            ipv6 = "- - -",
            wifiInterface = "- - -",
            linkSpeed = "${wifiInfo.linkSpeed} Mbps",
            frequency = "${wifiInfo.frequency} MHz",
            wifiFeatures = "- - -",
            mobileDataStatus = if (telephonyManager.dataState == TelephonyManager.DATA_CONNECTED) "Connected" else "Disconnected",
            multiSim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) telephonyManager.phoneCount.toString() else "1",
            deviceType = "- - -",
            sim1Name = telephonyManager.simOperatorName ?: "- - -",
            sim1PhoneNumber = "- - -",
            sim1CountryIso = telephonyManager.simCountryIso ?: "- - -",
            sim1Mcc = if ((telephonyManager.simOperator?.length ?: 0) >= 3) telephonyManager.simOperator?.substring(0, 3) ?: "- - -" else "- - -",
            sim1Mnc = if ((telephonyManager.simOperator?.length ?: 0) > 3) telephonyManager.simOperator?.substring(3) ?: "- - -" else "- - -",
            sim1CarrierId = "- - -",
            sim1CarrierName = telephonyManager.simOperatorName ?: "- - -",
            sim1DataRoaming = if (telephonyManager.isNetworkRoaming) "Enabled" else "Disabled",
            sim2Name = "- - -",
            sim2PhoneNumber = "- - -",
            sim2CountryIso = "- - -",
            sim2Mcc = "- - -",
            sim2Mnc = "- - -",
            sim2CarrierId = "- - -",
            sim2CarrierName = "- - -",
            sim2DataRoaming = "- - -"
        )
    }

    override fun getDisplay(): Display {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        val displayMetrics = android.util.DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return Display(
            name = "Default",
            screenHeight = displayMetrics.heightPixels.toString(),
            screenWidth = displayMetrics.widthPixels.toString(),
            screenSize = "- - -",
            physicalSize = "- - -",
            defaultOrientation = if (context.resources.configuration.orientation == 1) "Portrait" else "Landscape",
            refreshRate = "${windowManager.defaultDisplay.refreshRate} Hz",
            hdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.isHdr.toString()
            } else {
                "Not Supported"
            },
            brightnessMode = "- - -",
            screenTimeout = "- - -",
            displayBucket = "- - -",
            displayDpi = displayMetrics.densityDpi.toString(),
            xdpi = displayMetrics.xdpi.toString(),
            ydpi = displayMetrics.ydpi.toString(),
            logicalDensity = displayMetrics.density.toString(),
            scaledDensity = displayMetrics.scaledDensity.toString(),
            fontScale = context.resources.configuration.fontScale.toString()
        )
    }

    override fun getSensors(): Sensors {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val sensorCount = sensorList.size

        return Sensors(
            sensorCountMessage = "$sensorCount sensors available",
            sensorTypeName = "- - -",
            name = "- - -",
            vendor = "- - -",
            wakeUpSensor = "- - -",
            power = "- - -"
        )
    }

    override fun getApps(): Apps {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val count = packages.size

        return Apps(
            appCount = "$count apps installed",
            appName = "- - -",
            packageName = "- - -",
            version = "- - -",
            apiLevelTag = "- - -",
            architectureTag = "- - -"
        )
    }

    override fun getLocation(): Location {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Location(
                beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
                qzss = "- - -", irnss = "- - -", sbas = "- - -", latitude = "Denied",
                longitude = "Denied", altitude = "Denied", seaLevelAltitude = "- - -",
                speed = "Denied", speedAccurate = "- - -", pdop = "- - -",
                timeToFirstFix = "- - -", ehvDop = "- - -", hvAccurate = "- - -",
                numberOfSatellites = "- - -", bearing = "Denied", bearingAccurate = "- - -"
            )
        }
        val lastKnownLocation = try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: 
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            null
        }

        return Location(
            beidou = "- - -", navstarGps = "- - -", galileo = "- - -", glonass = "- - -",
            qzss = "- - -", irnss = "- - -", sbas = "- - -",
            latitude = lastKnownLocation?.latitude?.toString() ?: "- - -",
            longitude = lastKnownLocation?.longitude?.toString() ?: "- - -",
            altitude = lastKnownLocation?.altitude?.toString() ?: "- - -",
            seaLevelAltitude = "- - -",
            speed = lastKnownLocation?.speed?.toString() ?: "- - -",
            speedAccurate = "- - -", pdop = "- - -", timeToFirstFix = "- - -",
            ehvDop = "- - -", hvAccurate = "- - -", numberOfSatellites = "- - -",
            bearing = lastKnownLocation?.bearing?.toString() ?: "- - -",
            bearingAccurate = "- - -"
        )
    }

    override fun getCamera(): Camera {
        return Camera(
            aberrationModes = "- - -", antibandingModes = "- - -", autoExposureModes = "- - -",
            targetFpsRanges = "- - -", compensationRange = "- - -", compensationStep = "- - -",
            autoFocusModes = "- - -", effects = "- - -", sceneModes = "- - -",
            videoStabilizationModes = "- - -", autoWhiteBalanceModes = "- - -",
            maxAutoExposureRegions = "- - -", maxAutoFocusRegions = "- - -",
            maxAutoWhiteBalanceRegions = "- - -", edgeModes = "- - -", flashAvailable = "- - -",
            hotPixelModes = "- - -", hardwareLevel = "- - -", thumbnailSizes = "- - -",
            lensPlacement = "- - -", apertures = "- - -", filterDensities = "- - -",
            focalLengths = "- - -", opticalStabilization = "- - -", focusDistanceCalibration = "- - -",
            cameraCapabilities = "- - -", maxOutputStreams = "- - -", maxOutputStreamsStalling = "- - -",
            maxRawOutputStreams = "- - -", partialResults = "- - -", maxDigitalZoom = "- - -",
            croppingType = "- - -", supportedResolutions = "- - -", testPatternModes = "- - -",
            colorFilterArrangement = "- - -", sensorSize = "- - -", pixelArraySize = "- - -",
            timestampSource = "- - -", orientation = "- - -"
        )
    }

    override fun getDashboard(): Dashboard {
        val device = getDevice()
        val system = getSystem()
        val storage = getStorage()
        val sensors = getSensors()
        val apps = getApps()

        val ramUsageParts = storage.usedTotalMemory.split(" / ")
        val usedRam = if (ramUsageParts.isNotEmpty()) ramUsageParts[0] else "- - -"
        val totalRam = if (ramUsageParts.size > 1) ramUsageParts[1] else "- - -"

        return Dashboard(
            deviceModel = device.model,
            osVersion = system.androidVersion,
            ramUsage = storage.usedTotalMemory,
            ramUsagePercentage = storage.usagePercentageRam,
            usedMemory = usedRam,
            totalMemory = totalRam,
            freeMemory = storage.freeMemory,
            socName = getSystemProperty("ro.board.platform").ifBlank { getCpuInfoMap()["Hardware"] ?: "- - -" },
            coreFrequencies = emptyList(),
            storageAnalysisMessage = storage.usedTotalFreeInternal,
            internalStorageUsage = storage.usagePercentageInternal,
            batteryStatus = getBattery().status,
            batteryVoltage = getBattery().voltage,
            batteryTemp = getBattery().temperature,
            sensorCount = sensors.sensorCountMessage,
            appCount = apps.appCount
        )
    }
}
