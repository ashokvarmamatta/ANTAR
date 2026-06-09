package com.ashes.dev.works.system.core.internals.antar.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Static colors for theme initialization
val StaticAntarCyan = Color(0xFF00E5FF)
val StaticAntarTeal = Color(0xFF00BFA5)
val StaticAntarBlue = Color(0xFF448AFF)
val StaticAntarPurple = Color(0xFFB388FF)
val StaticAntarPink = Color(0xFFFF80AB)
val StaticAntarDark = Color(0xFF0A0E21)
val StaticAntarSurface = Color(0xFF111631)
val StaticAntarSurfaceElevated = Color(0xFF1A1F3D)
val StaticAntarCard = Color(0xFF161B35)
val StaticAntarWhite = Color(0xFFF0F0F5)
val StaticAntarGray = Color(0xFF8F93A2)
val StaticAntarDimGray = Color(0xFF5A5E6F)

// Dynamic theme-aware color properties
val AntarCyan = StaticAntarCyan
val AntarTeal = StaticAntarTeal
val AntarBlue = StaticAntarBlue
val AntarPurple = StaticAntarPurple
val AntarPink = StaticAntarPink

val AntarDark: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val AntarSurface: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val AntarSurfaceElevated: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val AntarCard: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

// Text
val AntarWhite: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

val AntarGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val AntarDimGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.outline

// Semantic
val AntarGreen = Color(0xFF69F0AE)
val AntarRed = Color(0xFFFF5252)
val AntarOrange = Color(0xFFFFAB40)
val AntarYellow = Color(0xFFFFFF00)

// Gradient colors for headers
val GradientStart = StaticAntarCyan
val GradientMid = StaticAntarBlue
val GradientEnd = StaticAntarPurple

// Card accent - subtle glow borders
val GlowCyan = StaticAntarCyan.copy(alpha = 0.2f)
val GlowPurple = StaticAntarPurple.copy(alpha = 0.2f)
