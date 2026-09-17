package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.BrightnessMode
import com.ashes.dev.works.system.core.internals.antar.domain.model.DisplayInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.ScreenOrientation
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class DisplayRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : DisplayRepository {

    override suspend fun getDisplayInfo(): AppResult<DisplayInfo> = withContext(io) {
        appResultOf {
            val displayManager = context.getSystemService(DisplayManager::class.java)
            val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
            val metrics = context.resources.displayMetrics
            val (widthPx, heightPx) = realSize(display)

            val widthIn = widthPx / metrics.xdpi.toDouble()
            val heightIn = heightPx / metrics.ydpi.toDouble()

            DisplayInfo(
                name = display.name,
                widthPx = widthPx,
                heightPx = heightPx,
                diagonalInches = sqrt(widthIn * widthIn + heightIn * heightIn),
                physicalWidthMm = widthIn * MM_PER_INCH,
                physicalHeightMm = heightIn * MM_PER_INCH,
                orientation = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ScreenOrientation.LANDSCAPE
                } else {
                    ScreenOrientation.PORTRAIT
                },
                refreshRateHz = display.refreshRate,
                isHdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) display.isHdr else null,
                brightnessMode = readBrightnessMode(),
                screenTimeoutSeconds = runCatching {
                    Settings.System.getLong(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT) / 1000
                }.getOrNull(),
                densityDpi = metrics.densityDpi,
                densityBucket = densityBucket(metrics.densityDpi),
                xdpi = metrics.xdpi,
                ydpi = metrics.ydpi,
                density = metrics.density,
                // scaledDensity is deprecated and wrong under Android 14+ non-linear font scaling.
                scaledDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, metrics),
                fontScale = context.resources.configuration.fontScale
            )
        }
    }

    /** Full panel size in pixels, independent of the app window (split screen, freeform). */
    private fun realSize(display: Display): Pair<Int, Int> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = context.getSystemService(WindowManager::class.java).maximumWindowMetrics.bounds
            bounds.width() to bounds.height()
        } else {
            val real = DisplayMetrics()
            @Suppress("DEPRECATION")
            display.getRealMetrics(real)
            real.widthPixels to real.heightPixels
        }

    private fun readBrightnessMode(): BrightnessMode? = runCatching {
        when (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)) {
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC -> BrightnessMode.AUTOMATIC
            else -> BrightnessMode.MANUAL
        }
    }.getOrNull()

    private fun densityBucket(dpi: Int): String? = when (dpi) {
        DisplayMetrics.DENSITY_LOW -> "ldpi"
        DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
        DisplayMetrics.DENSITY_HIGH -> "hdpi"
        DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
        DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
        DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
        else -> null
    }

    private companion object {
        const val MM_PER_INCH = 25.4
    }
}
