package com.ashes.dev.works.system.core.internals.antar.domain.model

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class MotionLevel { LOW, MEDIUM, HIGH }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColors: Boolean = false,
    val accentIndex: Int = 0,
    val motionLevel: MotionLevel = MotionLevel.HIGH,
    val introSeen: Boolean = false,
    val appsConsentGiven: Boolean = false
)
