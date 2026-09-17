package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuCore
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfoField
import com.ashes.dev.works.system.core.internals.antar.domain.model.GpuInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLSurface

class CpuRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : CpuRepository {

    /** Facts that cannot change while the process lives. Guarded by [cacheMutex]. */
    private var cachedStatic: StaticCpu? = null
    private val cacheMutex = Mutex()

    override suspend fun getCpuInfo(): AppResult<CpuInfo> = withContext(io) {
        appResultOf {
            val static = staticCpu()
            CpuInfo(
                socName = static.socName,
                hardware = static.hardware,
                coreCount = Runtime.getRuntime().availableProcessors(),
                minFrequencyKhz = readPositiveLong(CPU0_FREQ_DIR + "cpuinfo_min_freq"),
                maxFrequencyKhz = readPositiveLong(CPU0_FREQ_DIR + "cpuinfo_max_freq"),
                currentFrequencyKhz = readPositiveLong(CPU0_FREQ_DIR + "scaling_cur_freq"),
                architecture = static.architecture,
                fabricationNm = static.fabricationNm,
                supportedAbis = static.supportedAbis,
                governor = readTrimmed(CPU0_FREQ_DIR + "scaling_governor") ?: static.cpuinfoGovernor,
                features = static.features,
                cores = static.cores,
                gpu = GpuInfo(
                    renderer = static.glRenderer,
                    vendor = static.glVendor,
                    openGlEsVersion = static.openGlEsVersion,
                    openGlExtensions = static.glExtensions,
                    vulkanHardwareLevel = static.vulkanHardwareLevel,
                    maxFrequencyHz = firstPositiveLong(GPU_MAX_FREQ_PATHS),
                    currentFrequencyHz = firstPositiveLong(GPU_CUR_FREQ_PATHS)
                )
            )
        }
    }

    /** Reads the static facts once. A failed read is not cached, so a retry reads again. */
    private suspend fun staticCpu(): StaticCpu = cacheMutex.withLock {
        cachedStatic ?: readStaticCpu().also { cachedStatic = it }
    }

    /** Must run on [io]: it reads /proc, forks `getprop` and creates a throwaway EGL context. */
    private fun readStaticCpu(): StaticCpu {
        val cpuinfo = readText("/proc/cpuinfo").orEmpty()
        val allFields = parseFields(cpuinfo)
        val cpuinfoMap = allFields.associate { it.key to it.value }

        val hardware = readSystemProperty("ro.board.platform") ?: cpuinfoMap["Hardware"]?.takeIf { it.isNotBlank() }
        val knownSoc = hardware?.let { KNOWN_SOCS[it.lowercase()] }
        val gl = readGlStrings()

        return StaticCpu(
            socName = knownSoc?.name ?: hardware,
            hardware = hardware,
            fabricationNm = knownSoc?.fabricationNm,
            architecture = System.getProperty("os.arch")?.takeIf { it.isNotBlank() },
            supportedAbis = Build.SUPPORTED_ABIS.toList(),
            cpuinfoGovernor = cpuinfoMap["CPU governor"]?.takeIf { it.isNotBlank() },
            features = allFields.firstOrNull { it.key == "Features" }
                ?.value
                ?.split(WHITESPACE)
                ?.filter { it.isNotEmpty() }
                .orEmpty(),
            cores = parseCores(cpuinfo),
            glRenderer = gl?.renderer,
            glVendor = gl?.vendor,
            glExtensions = gl?.extensions,
            openGlEsVersion = context.getSystemService(ActivityManager::class.java)
                ?.deviceConfigurationInfo
                ?.glEsVersion
                ?.takeIf { it.isNotBlank() },
            vulkanHardwareLevel = vulkanHardwareLevel()
        )
    }

    private fun parseCores(cpuinfo: String): List<CpuCore> =
        cpuinfo.split(BLANK_LINE).mapNotNull { block ->
            val fields = parseFields(block)
            val processor = fields.firstOrNull { it.key == "processor" }?.value?.toIntOrNull()
            processor?.let { CpuCore(processor = it, fields = fields) }
        }

    /** `key : value` lines; the value keeps any further colons. */
    private fun parseFields(text: String): List<CpuInfoField> =
        text.lines().mapNotNull { line ->
            val colon = line.indexOf(':')
            if (colon <= 0) return@mapNotNull null
            val key = line.substring(0, colon).trim()
            val value = line.substring(colon + 1).trim()
            if (key.isEmpty()) null else CpuInfoField(key, value)
        }

    private fun readGlStrings(): GlStrings? {
        val egl = EGLContext.getEGL() as? EGL10 ?: return null
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        if (display == EGL10.EGL_NO_DISPLAY || !egl.eglInitialize(display, IntArray(2))) return null

        var surface: EGLSurface = EGL10.EGL_NO_SURFACE
        var glContext: EGLContext = EGL10.EGL_NO_CONTEXT
        return try {
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!egl.eglChooseConfig(display, CONFIG_SPEC, configs, 1, numConfigs) || numConfigs[0] == 0) {
                return null
            }
            val config = configs[0] ?: return null
            surface = egl.eglCreatePbufferSurface(display, config, SURFACE_ATTRIBS)
            glContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, CONTEXT_ATTRIBS)
            if (surface == EGL10.EGL_NO_SURFACE || glContext == EGL10.EGL_NO_CONTEXT) return null
            if (!egl.eglMakeCurrent(display, surface, surface, glContext)) return null

            GlStrings(
                renderer = GLES20.glGetString(GLES20.GL_RENDERER)?.takeIf { it.isNotBlank() },
                vendor = GLES20.glGetString(GLES20.GL_VENDOR)?.takeIf { it.isNotBlank() },
                extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)?.trim()?.takeIf { it.isNotEmpty() }
            )
        } catch (e: RuntimeException) {
            null
        } finally {
            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
            if (surface != EGL10.EGL_NO_SURFACE) egl.eglDestroySurface(display, surface)
            if (glContext != EGL10.EGL_NO_CONTEXT) egl.eglDestroyContext(display, glContext)
            egl.eglTerminate(display)
        }
    }

    private fun vulkanHardwareLevel(): Int? {
        val pm = context.packageManager
        return when {
            pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL, 1) -> 1
            pm.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL) -> 0
            else -> null
        }
    }

    private fun firstPositiveLong(paths: List<String>): Long? = paths.firstNotNullOfOrNull { readPositiveLong(it) }

    private fun readPositiveLong(path: String): Long? = readTrimmed(path)?.toLongOrNull()?.takeIf { it > 0L }

    private fun readTrimmed(path: String): String? = readText(path)?.trim()?.takeIf { it.isNotEmpty() }

    /** Null when the file is missing or unreadable (SELinux denies many sysfs nodes to apps). */
    private fun readText(path: String): String? = try {
        File(path).readText()
    } catch (e: Exception) {
        null
    }

    /** Raw `getprop` output, or null when the property is unset or cannot be read. */
    private fun readSystemProperty(key: String): String? = try {
        val process = ProcessBuilder("getprop", key).start()
        try {
            process.inputStream.bufferedReader().use { it.readLine() }?.trim()?.takeIf { it.isNotEmpty() }
        } finally {
            process.destroy()
        }
    } catch (e: Exception) {
        null
    }

    private data class StaticCpu(
        val socName: String?,
        val hardware: String?,
        val fabricationNm: Int?,
        val architecture: String?,
        val supportedAbis: List<String>,
        val cpuinfoGovernor: String?,
        val features: List<String>,
        val cores: List<CpuCore>,
        val glRenderer: String?,
        val glVendor: String?,
        val glExtensions: String?,
        val openGlEsVersion: String?,
        val vulkanHardwareLevel: Int?
    )

    private data class GlStrings(val renderer: String?, val vendor: String?, val extensions: String?)

    private data class KnownSoc(val name: String, val fabricationNm: Int?)

    private companion object {
        const val CPU0_FREQ_DIR = "/sys/devices/system/cpu/cpu0/cpufreq/"

        const val EGL_OPENGL_ES2_BIT = 4
        const val EGL_RENDERABLE_TYPE = 0x3040
        const val EGL_CONTEXT_CLIENT_VERSION = 0x3098

        val WHITESPACE = Regex("\\s+")
        val BLANK_LINE = Regex("\\n\\s*\\n")

        /** Platform id -> marketed product name. Product data, not UI copy. */
        val KNOWN_SOCS = mapOf(
            "mt6897" to KnownSoc(name = "MediaTek Dimensity 8300", fabricationNm = 4)
        )

        val CONFIG_SPEC = intArrayOf(
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_NONE
        )
        val SURFACE_ATTRIBS = intArrayOf(EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE)
        val CONTEXT_ATTRIBS = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)

        /** Current GPU clock in Hz, by vendor driver. */
        val GPU_CUR_FREQ_PATHS = listOf(
            "/sys/class/kgsl/kgsl-3d0/gpuclk",
            "/sys/class/devfreq/fde60000.gpu/cur_freq",
            "/sys/kernel/gpu/gpu_clock",
            "/sys/class/mali-km/mali0/clock",
            "/sys/class/pvr/devices/pvr/gpu_clock",
            "/sys/class/devfreq/18000000.qcom,kgsl-3d0/cur_freq",
            "/sys/class/devfreq/1c50000.mali/cur_freq",
            "/sys/class/mali/dvfs/gpufreq"
        )

        /** Maximum GPU clock in Hz, by vendor driver. */
        val GPU_MAX_FREQ_PATHS = listOf(
            "/sys/class/kgsl/kgsl-3d0/max_gpuclk",
            "/sys/class/devfreq/fde60000.gpu/max_freq",
            "/sys/kernel/gpu/gpu_max_clock",
            "/sys/class/mali-km/mali0/max_clock",
            "/sys/class/pvr/devices/pvr/gpu_max_clock",
            "/sys/class/devfreq/18000000.qcom,kgsl-3d0/max_freq",
            "/sys/class/devfreq/1c50000.mali/max_freq",
            "/sys/class/mali/dvfs/gpufreq_max"
        )
    }
}
