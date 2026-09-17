package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.media.MediaDrm
import android.os.Build
import android.os.SystemClock
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.CalendarDate
import com.ashes.dev.works.system.core.internals.antar.domain.model.SelinuxMode
import com.ashes.dev.works.system.core.internals.antar.domain.model.SystemInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WidevineInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.net.ssl.SSLContext

/**
 * Operating-system facts. The expensive part (shell `getprop`/`mount`, MediaDrm, root probes) is
 * read once and cached behind a [Mutex]; uptime, locale and time zone are re-read on every call.
 */
class SystemRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : SystemRepository {

    private val cacheLock = Mutex()
    private var cached: SystemInfo? = null

    override suspend fun getSystemInfo(): AppResult<SystemInfo> = withContext(io) {
        appResultOf {
            val base = cacheLock.withLock { cached ?: readStaticInfo().also { cached = it } }
            base.copy(
                uptimeMillis = SystemClock.elapsedRealtime(),
                languageTag = Locale.getDefault().toLanguageTag(),
                timeZoneId = TimeZone.getDefault().id
            )
        }
    }

    override fun observeUptimeMillis(): Flow<Long> = flow {
        while (true) {
            emit(SystemClock.elapsedRealtime())
            delay(UPTIME_TICK_MS)
        }
    }

    private fun readStaticInfo(): SystemInfo {
        val sdk = Build.VERSION.SDK_INT
        return SystemInfo(
            androidVersion = Build.VERSION.RELEASE,
            codename = Build.VERSION.CODENAME?.takeIf { it.isNotBlank() && it != RELEASE_CODENAME },
            apiLevel = sdk,
            releaseDate = releaseDate(sdk),
            buildNumber = Build.DISPLAY,
            buildTimeMillis = Build.TIME,
            buildId = Build.ID,
            securityPatch = Build.VERSION.SECURITY_PATCH?.takeIf { it.isNotBlank() },
            baseband = Build.getRadioVersion()?.takeIf { it.isNotBlank() },
            languageTag = Locale.getDefault().toLanguageTag(),
            timeZoneId = TimeZone.getDefault().id,
            isRooted = isRooted(),
            uptimeMillis = SystemClock.elapsedRealtime(),
            isSystemAsRoot = isSystemAsRoot(),
            isSeamlessUpdateSupported = isSeamlessUpdateSupported(),
            isDynamicPartitionsEnabled = systemProperty("ro.boot.dynamic_partitions").isTruthy(),
            isTrebleEnabled = systemProperty("ro.treble.enabled").isTruthy(),
            vmName = System.getProperty("java.vm.name")?.takeIf { it.isNotBlank() },
            vmVersion = System.getProperty("java.vm.version")?.takeIf { it.isNotBlank() },
            vmMaxHeapBytes = Runtime.getRuntime().maxMemory(),
            kernelArchitecture = System.getProperty("os.arch")?.takeIf { it.isNotBlank() },
            kernelVersion = System.getProperty("os.version")?.takeIf { it.isNotBlank() },
            openGlEsVersion = openGlEsVersion(),
            selinuxMode = selinuxMode(),
            sslProvider = sslProvider(),
            widevine = widevineInfo()
        )
    }

    /** First public release of each API level from minSdk 24 upwards. */
    private fun releaseDate(sdk: Int): CalendarDate? = when (sdk) {
        24 -> CalendarDate(2016, 8, 22)
        25 -> CalendarDate(2016, 10, 4)
        26 -> CalendarDate(2017, 8, 21)
        27 -> CalendarDate(2017, 12, 5)
        28 -> CalendarDate(2018, 8, 6)
        29 -> CalendarDate(2019, 9, 3)
        30 -> CalendarDate(2020, 9, 8)
        31 -> CalendarDate(2021, 10, 4)
        32 -> CalendarDate(2022, 3, 7)
        33 -> CalendarDate(2022, 8, 15)
        34 -> CalendarDate(2023, 10, 4)
        35 -> CalendarDate(2024, 9, 3)
        36 -> CalendarDate(2025, 6, 10)
        else -> null
    }

    private fun isRooted(): Boolean {
        if (Build.TAGS?.contains("test-keys") == true) return true
        return SU_PATHS.any { path -> runCatching { File(path).exists() }.getOrDefault(false) }
    }

    private fun isSystemAsRoot(): Boolean {
        val mountedAsRoot = runShell("mount") { lines ->
            lines.any { it.contains(" / ") && !it.contains("rootfs") }
        } ?: false
        return mountedAsRoot || systemProperty("ro.build.system_root_image").isTruthy()
    }

    private fun isSeamlessUpdateSupported(): Boolean =
        !systemProperty("ro.boot.slot_suffix").isNullOrBlank() || systemProperty("ro.build.ab_update").isTruthy()

    private fun openGlEsVersion(): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        return activityManager?.deviceConfigurationInfo?.glEsVersion?.takeIf { it.isNotBlank() }
    }

    private fun selinuxMode(): SelinuxMode? =
        when (runShell("getenforce") { it.firstOrNull() }?.trim()?.lowercase(Locale.ROOT)) {
            "enforcing" -> SelinuxMode.ENFORCING
            "permissive" -> SelinuxMode.PERMISSIVE
            "disabled" -> SelinuxMode.DISABLED
            else -> null
        }

    private fun sslProvider(): String? = runCatching {
        val info = SSLContext.getDefault().provider.info ?: return@runCatching null
        val clean = info.replace(WHITESPACE, " ").trim()
        when {
            clean.contains("OpenSSL") ->
                OPENSSL_VERSION.find(clean)?.let { "OpenSSL " + it.groupValues[1] } ?: "OpenSSL"
            clean.contains("BoringSSL") -> "BoringSSL"
            else -> clean.takeIf { it.isNotBlank() }
        }
    }.getOrNull()

    private fun widevineInfo(): WidevineInfo? {
        val supported = runCatching { MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID) }.getOrDefault(false)
        if (!supported) return null
        return runCatching {
            val drm = MediaDrm(WIDEVINE_UUID)
            try {
                fun prop(name: String): String? =
                    runCatching { drm.getPropertyString(name) }.getOrNull()?.takeIf { it.isNotBlank() }

                WidevineInfo(
                    vendor = prop(MediaDrm.PROPERTY_VENDOR),
                    version = prop(MediaDrm.PROPERTY_VERSION),
                    description = prop(MediaDrm.PROPERTY_DESCRIPTION),
                    algorithms = prop(MediaDrm.PROPERTY_ALGORITHMS),
                    securityLevel = prop("securityLevel"),
                    systemId = prop("systemId"),
                    hdcpLevel = prop("hdcpLevel"),
                    maxHdcpLevel = prop("maxHdcpLevel"),
                    usageReportingSupported = prop("usageReportingSupport")?.lowercase(Locale.ROOT)?.toBooleanStrictOrNull(),
                    maxSessionCount = prop("maxNumberOfSessions")?.toIntOrNull(),
                    openSessionCount = prop("numberOfOpenSessions")?.toIntOrNull()
                )
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    drm.close()
                } else {
                    @Suppress("DEPRECATION")
                    drm.release()
                }
            }
        }.getOrNull()
    }

    /** Raw `getprop` value, or null when missing or unreadable. */
    private fun systemProperty(key: String): String? =
        runShell("getprop $key") { it.firstOrNull() }?.trim()?.takeIf { it.isNotEmpty() }

    private fun String?.isTruthy(): Boolean = this == "1" || this.equals("true", ignoreCase = true)

    private fun <T> runShell(command: String, parse: (List<String>) -> T): T? = runCatching {
        val process = Runtime.getRuntime().exec(command)
        try {
            parse(process.inputStream.bufferedReader().use { it.readLines() })
        } finally {
            process.destroy()
        }
    }.getOrNull()

    private companion object {
        const val UPTIME_TICK_MS = 1_000L
        const val RELEASE_CODENAME = "REL"
        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
        val WHITESPACE = Regex("\\s+")
        val OPENSSL_VERSION = Regex("OpenSSL\\s+([\\d.\\w]+)")
        val SU_PATHS = listOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
    }
}
