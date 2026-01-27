package com.ashes.dev.works.system.core.internals.antar.data.repository

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
            usbDebugging = "Disabled"
        )
    }

    override fun getSystem(): System {
        return System(
            androidVersion = "14",
            codename = "Upside Down Cake",
            releaseDate = "October 4, 2023",
            versionName = "14",
            apiLevel = "34",
            buildNumber = "UD1A.230803.041",
            buildTime = "- - -",
            buildId = "10811862",
            securityPatchLevel = "- - -",
            baseband = "- - -",
            language = "English (United States)",
            timeZone = "- - -",
            rootAccess = "No",
            systemUptime = "- - -",
            systemAsRoot = "- - -",
            seamlessUpdates = "- - -",
            dynamicPartitions = "- - -",
            projectTreble = "- - -",
            javaRuntime = "- - -",
            javaVm = "- - -",
            javaVmStackSize = "- - -",
            kernelArchitecture = "- - -",
            kernelVersion = "- - -",
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
        return Cpu(
            socName = "Google Tensor G3",
            cores = "9",
            frequencyRange = "- - -",
            processor = "- - -",
            struct = "- - -",
            frequency = "- - -",
            fabrication = "- - -",
            supportedAbis = "- - -",
            cpuHardware = "- - -",
            cpuGovernor = "- - -",
            procCpuinfo = "- - -",
            gpuRenderer = "- - -",
            gpuVendor = "- - -",
            openGlEs = "- - -",
            openGlExtensions = "- - -",
            vulkan = "- - -",
            gpuFrequency = "- - -",
            currentGpuFrequency = "- - -"
        )
    }

    override fun getLocation(): Location {
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

    override fun getNetwork(): Network {
        return Network(
            connectionType = "- - -",
            statusDescription = "- - -",
            signalStrength = "- - -",
            wifiStatus = "- - -",
            wifiSafety = "- - -",
            bssid = "- - -",
            dhcp = "- - -",
            dhcpLeaseDuration = "- - -",
            gateway = "- - -",
            netmask = "- - -",
            dns1 = "- - -",
            dns2 = "- - -",
            ip = "- - -",
            ipv6 = "- - -",
            wifiInterface = "- - -",
            linkSpeed = "- - -",
            frequency = "- - -",
            wifiFeatures = "- - -",
            mobileDataStatus = "- - -",
            multiSim = "- - -",
            deviceType = "- - -",
            sim1Name = "- - -",
            sim1PhoneNumber = "- - -",
            sim1CountryIso = "- - -",
            sim1Mcc = "- - -",
            sim1Mnc = "- - -",
            sim1CarrierId = "- - -",
            sim1CarrierName = "- - -",
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

    override fun getStorage(): Storage {
        return Storage(
            freeMemory = "- - -",
            usedTotalMemory = "- - -",
            usagePercentageRam = "- - -",
            internalStoragePath = "- - -",
            usedTotalFreeInternal = "- - -",
            usagePercentageInternal = "- - -",
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
        return Battery(
            batteryLevel = "- - -",
            status = "- - -",
            current = "- - -",
            power = "- - -",
            temperature = "- - -",
            health = "- - -",
            powerSource = "- - -",
            technology = "- - -",
            voltage = "- - -",
            capacity = "- - -",
            dualCellDevice = "- - -"
        )
    }

    override fun getDisplay(): Display {
        return Display(
            name = "- - -",
            screenHeight = "- - -",
            screenWidth = "- - -",
            screenSize = "- - -",
            physicalSize = "- - -",
            defaultOrientation = "- - -",
            refreshRate = "- - -",
            hdr = "- - -",
            brightnessMode = "- - -",
            screenTimeout = "- - -",
            displayBucket = "- - -",
            displayDpi = "- - -",
            xdpi = "- - -",
            ydpi = "- - -",
            logicalDensity = "- - -",
            scaledDensity = "- - -",
            fontScale = "- - -"
        )
    }

    override fun getSensors(): Sensors {
        return Sensors(
            sensorCountMessage = "- - -",
            sensorTypeName = "- - -",
            name = "- - -",
            vendor = "- - -",
            wakeUpSensor = "- - -",
            power = "- - -"
        )
    }

    override fun getApps(): Apps {
        return Apps(
            appCount = "- - -",
            appName = "- - -",
            packageName = "- - -",
            version = "- - -",
            apiLevelTag = "- - -",
            architectureTag = "- - -"
        )
    }

    override fun getCamera(): Camera {
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
        return Dashboard(
            deviceModel = "Pixel 8 Pro",
            osVersion = "14",
            ramUsage = "- - -",
            ramUsagePercentage = "- - -",
            usedMemory = "- - -",
            totalMemory = "- - -",
            freeMemory = "- - -",
            socName = "Google Tensor G3",
            coreFrequencies = emptyList(),
            storageAnalysisMessage = "- - -",
            internalStorageUsage = "- - -",
            batteryStatus = "- - -",
            batteryVoltage = "- - -",
            batteryTemp = "- - -",
            sensorCount = "- - -",
            appCount = "- - -"
        )
    }
}