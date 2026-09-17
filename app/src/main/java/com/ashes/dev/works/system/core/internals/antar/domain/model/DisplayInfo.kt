package com.ashes.dev.works.system.core.internals.antar.domain.model

enum class ScreenOrientation { PORTRAIT, LANDSCAPE }

enum class BrightnessMode { AUTOMATIC, MANUAL }

/** Raw display facts. Units are in the field names; the UI formats and labels them. */
data class DisplayInfo(
    val name: String?,
    val widthPx: Int,
    val heightPx: Int,
    val diagonalInches: Double,
    val physicalWidthMm: Double,
    val physicalHeightMm: Double,
    val orientation: ScreenOrientation,
    val refreshRateHz: Float,
    /** Null below Android 8.0, where the platform does not report it. */
    val isHdr: Boolean?,
    val brightnessMode: BrightnessMode?,
    val screenTimeoutSeconds: Long?,
    val densityDpi: Int,
    /** Android density bucket id such as xxhdpi: a technical token, not prose. */
    val densityBucket: String?,
    val xdpi: Float,
    val ydpi: Float,
    val density: Float,
    val scaledDensity: Float,
    val fontScale: Float
)
