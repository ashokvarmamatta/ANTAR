package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import java.io.File
import java.lang.System.getProperty

class CpuRepositoryImpl(private val context: Context) : CpuRepository {
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

    private fun getSystemProperty(key: String): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
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
}
