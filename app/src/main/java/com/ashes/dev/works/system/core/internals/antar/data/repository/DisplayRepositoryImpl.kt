package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.Display
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository
import java.util.Locale
import kotlin.math.sqrt

class DisplayRepositoryImpl(private val context: Context) : DisplayRepository {
    override fun getDisplay(): Display {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getRealMetrics(displayMetrics)

        val x = (displayMetrics.widthPixels / displayMetrics.xdpi).toDouble()
        val y = (displayMetrics.heightPixels / displayMetrics.ydpi).toDouble()
        val screenInches = sqrt(x * x + y * y)
        
        val physicalWidth = (displayMetrics.widthPixels / displayMetrics.xdpi) * 25.4
        val physicalHeight = (displayMetrics.heightPixels / displayMetrics.ydpi) * 25.4

        val brightnessMode = try {
            val mode = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) "Automatic" else "Manual"
        } catch (_: Exception) {
            "Unknown"
        }

        val screenTimeout = try {
            val timeout = Settings.System.getLong(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
            "${timeout / 1000}s"
        } catch (_: Exception) {
            "Unknown"
        }

        val densityBucket = when (displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "ldpi"
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_XHIGH -> "xhdpi"
            DisplayMetrics.DENSITY_XXHIGH -> "xxhdpi"
            DisplayMetrics.DENSITY_XXXHIGH -> "xxxhdpi"
            else -> "unknown"
        }

        return Display(
            name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) display.name else "Default",
            screenHeight = displayMetrics.heightPixels.toString(),
            screenWidth = displayMetrics.widthPixels.toString(),
            screenSize = String.format(Locale.US, "%.2f\"", screenInches),
            physicalSize = String.format(Locale.US, "%.1f x %.1f mm", physicalWidth, physicalHeight),
            defaultOrientation = if (context.resources.configuration.orientation == 1) "Portrait" else "Landscape",
            refreshRate = String.format(Locale.US, "%.2f Hz", display.refreshRate),
            hdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                display.isHdr.toString()
            } else {
                "Not Supported"
            },
            brightnessMode = brightnessMode,
            screenTimeout = screenTimeout,
            displayBucket = densityBucket,
            displayDpi = displayMetrics.densityDpi.toString(),
            xdpi = String.format(Locale.US, "%.3f", displayMetrics.xdpi),
            ydpi = String.format(Locale.US, "%.3f", displayMetrics.ydpi),
            logicalDensity = displayMetrics.density.toString(),
            scaledDensity = displayMetrics.scaledDensity.toString(),
            fontScale = context.resources.configuration.fontScale.toString()
        )
    }
}
