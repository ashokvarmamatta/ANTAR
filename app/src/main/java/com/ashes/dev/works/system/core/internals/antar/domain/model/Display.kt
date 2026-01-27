package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Display(
    val name: String,
    val screenHeight: String,
    val screenWidth: String,
    val screenSize: String,
    val physicalSize: String,
    val defaultOrientation: String,
    val refreshRate: String,
    val hdr: String,
    val brightnessMode: String,
    val screenTimeout: String,
    val displayBucket: String,
    val displayDpi: String,
    val xdpi: String,
    val ydpi: String,
    val logicalDensity: String,
    val scaledDensity: String,
    val fontScale: String
)