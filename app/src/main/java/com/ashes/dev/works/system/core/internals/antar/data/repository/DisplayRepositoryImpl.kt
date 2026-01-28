package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.domain.model.Display
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository

class DisplayRepositoryImpl(private val context: Context) : DisplayRepository {
    override fun getDisplay(): Display {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        val displayMetrics = android.util.DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return Display(
            name = "Default",
            screenHeight = displayMetrics.heightPixels.toString(),
            screenWidth = displayMetrics.widthPixels.toString(),
            screenSize = "- - -",
            physicalSize = "- - -",
            defaultOrientation = if (context.resources.configuration.orientation == 1) "Portrait" else "Landscape",
            refreshRate = "${windowManager.defaultDisplay.refreshRate} Hz",
            hdr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.isHdr.toString()
            } else {
                "Not Supported"
            },
            brightnessMode = "- - -",
            screenTimeout = "- - -",
            displayBucket = "- - -",
            displayDpi = displayMetrics.densityDpi.toString(),
            xdpi = displayMetrics.xdpi.toString(),
            ydpi = displayMetrics.ydpi.toString(),
            logicalDensity = displayMetrics.density.toString(),
            scaledDensity = displayMetrics.scaledDensity.toString(),
            fontScale = context.resources.configuration.fontScale.toString()
        )
    }
}
