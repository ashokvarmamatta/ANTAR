package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.telephony.TelephonyManager
import android.view.WindowManager
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
import java.io.File
import java.lang.System.getProperty

class DeviceRepositoryImpl(private val context: Context) : DeviceRepository {
    override fun getDevice(): Device {
        return Device(
            deviceName = Build.MODEL,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            device = Build.DEVICE,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            brand = Build.BRAND,
            googleAdvertisingId = "- - -",
            androidDeviceId = "- - -",
            hardwareSerial = Build.SERIAL,
            buildFingerprint = Build.FINGERPRINT,
            deviceType = "- - -",
            networkOperator = "- - -",
            networkType = "- - -",
            wifiMacAddress = "- - -",
            bluetoothMacAddress = "- - -",
            usbDebugging = "- - -"
        )
    }

    override fun getSystem(): System {
        return System(
            androidVersion = Build.VERSION.RELEASE,
            codename = Build.VERSION.CODENAME,
            releaseDate = "- - -",
            versionName = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT.toString(),
            buildNumber = Build.DISPLAY,
            buildTime = Build.TIME.toString(),
            buildId = Build.ID,
            securityPatchLevel = Build.VERSION.SECURITY_PATCH,
            baseband = Build.getRadioVersion(),
            language = java.util.Locale.getDefault().toString(),
            timeZone = java.util.TimeZone.getDefault().id,
            rootAccess = "- - -",
            systemUptime = "- - -",
            systemAsRoot = "- - -",
            seamlessUpdates = "- - -",
            dynamicPartitions = "- - -",
            projectTreble = "- - -",
            javaRuntime = "- - -",
            javaVm = "- - -",
            javaVmStackSize = "- - -",
            kernelArchitecture = getProperty("os.arch") ?: "- - -",
            kernelVersion = getProperty("os.version") ?: "- - -",
            openGlEs = "- - -",
            selinux = "- - -",
            openSslVersion = "- - -",
            drmVendor = "- - -",
            drmVersion = "- - -",
            drmDescription = "- - -",
            drmAlgorithm = "- - -",
            drmSecurityLevel = "- - -",
            drmSystemId = "- - -",
            drmHdcpLevel = "- - -",
            drmMaxHdcpLevel = "- - -",
            drmUsageReportingSupport = "- - -",
            drmMaxNumberOfSessions = "- - -",
            drmNumberOfOpenSessions = "- - -"
        )
    }

    override fun getCpu(): Cpu {
        val cpuInfo = File("/proc/cpuinfo").readText()
        return Cpu(
            socName = "- - -",
            cores = Runtime.getRuntime().availableProcessors().toString(),
            frequencyRange = "- - -",
            processor = "- - -",
            struct = "- - -",
            frequency = "- - -",
            fabrication = "- - -",
            supportedAbis = Build.SUPPORTED_ABIS.joinToString(),
            cpuHardware = "- - -",
            cpuGovernor = "- - -",
            procCpuinfo = cpuInfo,
            gpuRenderer = "- - -",
            gpuVendor = "- - -",
            openGlEs = "- - -",
            openGlExtensions = "- - -",
            vulkan = "- - -",
            gpuFrequency = "- - -",
            currentGpuFrequency = "- - -"
        )
    }

    override fun getStorage(): Storage {
        val internalStatFs = StatFs(Environment.getDataDirectory().path)
        val totalInternal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val freeInternal = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        val usedInternal = totalInternal - freeInternal

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val freeRam = memoryInfo.availMem
        val usedRam = totalRam - freeRam

        return Storage(
            freeMemory = freeRam.toString(),
            usedTotalMemory = "$usedRam / $totalRam",
            usagePercentageRam = "${(usedRam.toDouble() / totalRam.toDouble() * 100).toInt()}%",
            internalStoragePath = Environment.getDataDirectory().path,
            usedTotalFreeInternal = "$usedInternal / $totalInternal / $freeInternal",
            usagePercentageInternal = "${(usedInternal.toDouble() / totalInternal.toDouble() * 100).toInt()}%",
            systemStorageFileSystemType = "- - -",
            systemStoragePath = "- - -",
            systemStorageUsageProgress = "- - -",
            usedTotalFreeSystem = "- - -",
            internalStorageDataFileSystemType = "- - -",
            internalStorageDataPath = "- - -",
            internalStorageDataUsageProgress = "- - -",
            usedTotalFreeInternalData = "- - -"
        )
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

        return Battery(
            batteryLevel = "${(batteryPct * 100).toInt()}%",
            status = if (isCharging) "Charging" else "Discharging",
            current = "- - -",
            power = "- - -",
            temperature = "${temperature / 10f}",
            health = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                else -> "Unknown"
            },
            powerSource = "- - -",
            technology = "- - -",
            voltage = "${voltage / 1000f}",
            capacity = "- - -",
            dualCellDevice = "- - -"
        )
    }
    
    override fun getNetwork(): Network {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return Network(
            connectionType = activeNetwork?.typeName ?: "- - -",
            statusDescription = activeNetwork?.state?.name ?: "- - -",
            signalStrength = wifiInfo.rssi.toString(),
            wifiStatus = "- - -",
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
            linkSpeed = wifiInfo.linkSpeed.toString(),
            frequency = wifiInfo.frequency.toString(),
            wifiFeatures = "- - -",
            mobileDataStatus = "- - -",
            multiSim = "- - -",
            deviceType = "- - -",
            sim1Name = telephonyManager.simOperatorName ?: "- - -",
            sim1PhoneNumber = "- - -",
            sim1CountryIso = telephonyManager.simCountryIso ?: "- - -",
            sim1Mcc = telephonyManager.simOperator?.substring(0, 3) ?: "- - -",
            sim1Mnc = telephonyManager.simOperator?.substring(3) ?: "- - -",
            sim1CarrierId = "- - -",
            sim1CarrierName = telephonyManager.simOperatorName ?: "- - -",
            sim1DataRoaming = "- - -",
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
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = android.util.DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return Display(
            name = "- - -",
            screenHeight = displayMetrics.heightPixels.toString(),
            screenWidth = displayMetrics.widthPixels.toString(),
            screenSize = "- - -",
            physicalSize = "- - -",
            defaultOrientation = "- - -",
            refreshRate = "- - -",
            hdr = "- - -",
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
        val appCount = packages.size

        return Apps(
            appCount = "$appCount apps installed",
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return Location(
                beidou = "- - -",
                navstarGps = "- - -",
                galileo = "- - -",
                glonass = "- - -",
                qzss = "- - -",
                irnss = "- - -",
                sbas = "- - -",
                latitude = "- - -",
                longitude = "- - -",
                altitude = "- - -",
                seaLevelAltitude = "- - -",
                speed = "- - -",
                speedAccurate = "- - -",
                pdop = "- - -",
                timeToFirstFix = "- - -",
                ehvDop = "- - -",
                hvAccurate = "- - -",
                numberOfSatellites = "- - -",
                bearing = "- - -",
                bearingAccurate = "- - -"
            )
        }
        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        return Location(
            beidou = "- - -",
            navstarGps = "- - -",
            galileo = "- - -",
            glonass = "- - -",
            qzss = "- - -",
            irnss = "- - -",
            sbas = "- - -",
            latitude = lastKnownLocation?.latitude.toString(),
            longitude = lastKnownLocation?.longitude.toString(),
            altitude = lastKnownLocation?.altitude.toString(),
            seaLevelAltitude = "- - -",
            speed = lastKnownLocation?.speed.toString(),
            speedAccurate = "- - -",
            pdop = "- - -",
            timeToFirstFix = "- - -",
            ehvDop = "- - -",
            hvAccurate = "- - -",
            numberOfSatellites = "- - -",
            bearing = lastKnownLocation?.bearing.toString(),
            bearingAccurate = "- - -"
        )
    }

    override fun getCamera(): Camera {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList
        return Camera(
            aberrationModes = "- - -",
            antibandingModes = "- - -",
            autoExposureModes = "- - -",
            targetFpsRanges = "- - -",
            compensationRange = "- - -",
            compensationStep = "- - -",
            autoFocusModes = "- - -",
            effects = "- - -",
            sceneModes = "- - -",
            videoStabilizationModes = "- - -",
            autoWhiteBalanceModes = "- - -",
            maxAutoExposureRegions = "- - -",
            maxAutoFocusRegions = "- - -",
            maxAutoWhiteBalanceRegions = "- - -",
            edgeModes = "- - -",
            flashAvailable = "- - -",
            hotPixelModes = "- - -",
            hardwareLevel = "- - -",
            thumbnailSizes = "- - -",
            lensPlacement = "- - -",
            apertures = "- - -",
            filterDensities = "- - -",
            focalLengths = "- - -",
            opticalStabilization = "- - -",
            focusDistanceCalibration = "- - -",
            cameraCapabilities = "- - -",
            maxOutputStreams = "- - -",
            maxOutputStreamsStalling = "- - -",
            maxRawOutputStreams = "- - -",
            partialResults = "- - -",
            maxDigitalZoom = "- - -",
            croppingType = "- - -",
            supportedResolutions = "- - -",
            testPatternModes = "- - -",
            colorFilterArrangement = "- - -",
            sensorSize = "- - -",
            pixelArraySize = "- - -",
            timestampSource = "- - -",
            orientation = "- - -"
        )
    }

    override fun getDashboard(): Dashboard {
        val device = getDevice()
        val system = getSystem()
        val storage = getStorage()
        val battery = getBattery()
        val sensors = getSensors()
        val apps = getApps()

        val ramUsageParts = storage.usedTotalMemory.split(" / ")
        val usedRam = ramUsageParts[0]
        val totalRam = ramUsageParts[1]

        return Dashboard(
            deviceModel = device.model,
            osVersion = system.androidVersion,
            ramUsage = storage.usedTotalMemory,
            ramUsagePercentage = storage.usagePercentageRam,
            usedMemory = usedRam,
            totalMemory = totalRam,
            freeMemory = storage.freeMemory,
            socName = "- - -", // Not available in Cpu model
            coreFrequencies = emptyList(),
            storageAnalysisMessage = storage.usedTotalFreeInternal,
            internalStorageUsage = storage.usagePercentageInternal,
            batteryStatus = battery.status,
            batteryVoltage = battery.voltage,
            batteryTemp = battery.temperature,
            sensorCount = sensors.sensorCountMessage,
            appCount = apps.appCount
        )
    }
}