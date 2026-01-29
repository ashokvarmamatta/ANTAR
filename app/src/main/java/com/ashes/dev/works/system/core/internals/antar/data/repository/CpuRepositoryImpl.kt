package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import java.io.File
import java.lang.System.getProperty
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class CpuRepositoryImpl(private val context: Context) : CpuRepository {
    override fun getCpu(): Cpu {
        val cpuInfoMap = getCpuInfoMap()
        val cpuInfo = try {
            parseCpuInfo(File("/proc/cpuinfo").readText())
        } catch (e: Exception) {
            Pair("- - -", emptyList())
        }

        val minFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq")
        val maxFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
        val curFreq = getCpuFreq("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")
        val governor = try {
            File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").readText().trim()
        } catch (e: Exception) {
            cpuInfoMap["CPU governor"] ?: "- - -"
        }
        val hardware = getSystemProperty("ro.board.platform").ifBlank { cpuInfoMap["Hardware"] ?: "- - -" }
        val socInfo = getSocInfo(hardware)


        val gpuInfo = getGpuInfo()
        val maxGpuFreq = getGpuFrequency("max")
        val curGpuFreq = getGpuFrequency("current")

        return Cpu(
            socName = socInfo.first,
            cores = Runtime.getRuntime().availableProcessors().toString(),
            frequencyRange = if (minFreq != "- - -" && maxFreq != "- - -") "$minFreq - $maxFreq" else "- - -",
            processor = socInfo.first,
            struct = getProperty("os.arch") ?: "- - -",
            frequency = curFreq,
            fabrication = socInfo.second,
            supportedAbis = Build.SUPPORTED_ABIS.joinToString(),
            cpuHardware = hardware,
            cpuGovernor = governor,
            features = cpuInfo.first,
            procCpuinfo = cpuInfo.second,
            gpuRenderer = gpuInfo["renderer"] ?: "- - -",
            gpuVendor = gpuInfo["vendor"] ?: "- - -",
            openGlEs = getOpenGlEsVersion(),
            openGlExtensions = gpuInfo["extensions"] ?: "- - -",
            vulkan = getVulkanSupport(),
            gpuFrequency = maxGpuFreq,
            currentGpuFrequency = curGpuFreq
        )
    }

    private fun parseCpuInfo(cpuInfo: String): Pair<String, List<Map<String, String>>> {
        val features = cpuInfo.lines().firstOrNull { it.startsWith("Features") }?.substringAfter(":")?.trim()?.replace(" ", ", ") ?: "- - -"
        val perCoreInfo = cpuInfo.split("\n\n").mapNotNull {
            val lines = it.lines()
            val processorLine = lines.firstOrNull { it.startsWith("processor") }
            if (processorLine != null) {
                val coreInfo = mutableMapOf<String, String>()
                lines.forEach { line ->
                    if (line.contains(":")) {
                        val parts = line.split(":")
                        if (parts.size == 2) {
                            coreInfo[parts[0].trim()] = parts[1].trim()
                        }
                    }
                }
                coreInfo
            } else {
                null
            }
        }
        return Pair(features, perCoreInfo)
    }


    private fun getSocInfo(hardware: String): Pair<String, String> {
        return when (hardware) {
            "mt6897" -> "MediaTek Dimensity 7200" to "4nm"
            else -> hardware to "- - -"
        }
    }

    private fun getGpuInfo(): Map<String, String> {
        return try {
            val info = mutableMapOf<String, String>()
            val egl = EGLContext.getEGL() as EGL10
            val display: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            egl.eglInitialize(display, null)

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            val configSpec = intArrayOf(
                0x3040, 4, // EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_NONE
            )
            egl.eglChooseConfig(display, configSpec, configs, 1, numConfigs)
            val surfaceAttribs = intArrayOf(EGL10.EGL_WIDTH, 64, EGL10.EGL_HEIGHT, 64, EGL10.EGL_NONE)
            val surface: EGLSurface = egl.eglCreatePbufferSurface(display, configs[0], surfaceAttribs)
            val contextAttribs = intArrayOf(0x3098, 2, EGL10.EGL_NONE) // EGL_CONTEXT_CLIENT_VERSION
            val context: EGLContext = egl.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, contextAttribs)
            egl.eglMakeCurrent(display, surface, surface, context)

            info["renderer"] = GLES20.glGetString(GLES20.GL_RENDERER)
            info["vendor"] = GLES20.glGetString(GLES20.GL_VENDOR)
            info["extensions"] = GLES20.glGetString(GLES20.GL_EXTENSIONS)

            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
            egl.eglDestroySurface(display, surface)
            egl.eglDestroyContext(display, context)
            egl.eglTerminate(display)

            info
        } catch (e: Exception) {
            emptyMap()
        }
    }


    private fun getVulkanSupport(): String {
        val pm = context.packageManager
        return when {
            pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL, 1) -> "Level 1"
            pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL) -> "Level 0"
            else -> "Not Supported"
        }
    }

    private fun getGpuFrequency(type: String): String {
        val paths = when (type) {
            "current" -> listOf(
                "/sys/class/kgsl/kgsl-3d0/gpuclk",
                "/sys/class/devfreq/fde60000.gpu/cur_freq",
                "/sys/kernel/gpu/gpu_clock",
                "/sys/class/mali-km/mali0/clock",
                "/sys/class/pvr/devices/pvr/gpu_clock",
                "/sys/class/devfreq/18000000.qcom,kgsl-3d0/cur_freq",
                "/sys/class/devfreq/1c50000.mali/cur_freq",
                "/sys/class/mali/dvfs/gpufreq"
            )
            "max" -> listOf(
                "/sys/class/kgsl/kgsl-3d0/max_gpuclk",
                "/sys/class/devfreq/fde60000.gpu/max_freq",
                "/sys/kernel/gpu/gpu_max_clock",
                "/sys/class/mali-km/mali0/max_clock",
                "/sys/class/pvr/devices/pvr/gpu_max_clock",
                "/sys/class/devfreq/18000000.qcom,kgsl-3d0/max_freq",
                "/sys/class/devfreq/1c50000.mali/max_freq",
                "/sys/class/mali/dvfs/gpufreq_max"
            )
            else -> return "- - -"
        }

        for (path in paths) {
            try {
                val freq = File(path).readText().trim().toLong()
                return "${freq / 1000000} MHz"
            } catch (e: Exception) {
                // Continue to the next path
            }
        }
        return "- - -"
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
        } catch (e: Exception) {
        }
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
