package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.media.MediaDrm
import com.ashes.dev.works.system.core.internals.antar.domain.model.System
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
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

class SystemRepositoryImpl(private val context: Context) : SystemRepository {

    private lateinit var cachedSystem: System

    override fun getSystem(): System {
        if (::cachedSystem.isInitialized) {
            return cachedSystem
        }

        val drmInfo = getWidevineInfo()
        val uptimeMillis = SystemClock.elapsedRealtime()

        cachedSystem = System(
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
        return cachedSystem
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
}
