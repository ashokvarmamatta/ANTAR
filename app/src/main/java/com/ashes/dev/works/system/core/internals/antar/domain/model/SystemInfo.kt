package com.ashes.dev.works.system.core.internals.antar.domain.model

/** SELinux mode as reported by `getenforce`. */
enum class SelinuxMode { ENFORCING, PERMISSIVE, DISABLED }

/** A calendar date without time zone; the UI formats it for the current locale. [month] is 1-12. */
data class CalendarDate(val year: Int, val month: Int, val day: Int)

/** Widevine DRM properties. Every value is reported by the DRM plugin and may be missing. */
data class WidevineInfo(
    val vendor: String?,
    val version: String?,
    val description: String?,
    val algorithms: String?,
    /** Security level token such as L1 or L3. */
    val securityLevel: String?,
    val systemId: String?,
    /** HDCP token such as HDCP-2.2. */
    val hdcpLevel: String?,
    val maxHdcpLevel: String?,
    val usageReportingSupported: Boolean?,
    val maxSessionCount: Int?,
    val openSessionCount: Int?
)

/** Raw operating-system facts. Units are in the field names; the UI formats and labels them. */
data class SystemInfo(
    /** Build.VERSION.RELEASE, e.g. "15". */
    val androidVersion: String,
    /** Build.VERSION.CODENAME of a pre-release build; null on release builds. */
    val codename: String?,
    val apiLevel: Int,
    /** First public release date of this API level; null when not in the known table. */
    val releaseDate: CalendarDate?,
    /** Build.DISPLAY. */
    val buildNumber: String,
    val buildTimeMillis: Long,
    val buildId: String,
    val securityPatch: String?,
    val baseband: String?,
    /** BCP 47 tag of the default locale, e.g. "en-US". */
    val languageTag: String,
    /** IANA zone id, e.g. "Europe/Berlin". */
    val timeZoneId: String,
    val isRooted: Boolean,
    /** Time since boot including deep sleep, sampled when this value was built. */
    val uptimeMillis: Long,
    val isSystemAsRoot: Boolean,
    val isSeamlessUpdateSupported: Boolean,
    val isDynamicPartitionsEnabled: Boolean,
    val isTrebleEnabled: Boolean,
    val vmName: String?,
    val vmVersion: String?,
    val vmMaxHeapBytes: Long,
    val kernelArchitecture: String?,
    val kernelVersion: String?,
    val openGlEsVersion: String?,
    /** Null when the mode could not be read. */
    val selinuxMode: SelinuxMode?,
    /** TLS provider name and version, e.g. "BoringSSL". */
    val sslProvider: String?,
    /** Null when Widevine is not supported. */
    val widevine: WidevineInfo?
)
