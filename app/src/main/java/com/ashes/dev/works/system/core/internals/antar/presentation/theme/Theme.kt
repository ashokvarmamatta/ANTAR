package com.ashes.dev.works.system.core.internals.antar.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AntarColorScheme = darkColorScheme(
    primary = StaticAntarCyan,
    onPrimary = StaticAntarDark,
    primaryContainer = StaticAntarTeal,
    onPrimaryContainer = StaticAntarWhite,
    secondary = StaticAntarBlue,
    onSecondary = StaticAntarWhite,
    secondaryContainer = StaticAntarSurfaceElevated,
    onSecondaryContainer = StaticAntarCyan,
    tertiary = StaticAntarPurple,
    onTertiary = StaticAntarDark,
    tertiaryContainer = Color(0xFF2D1F5E),
    onTertiaryContainer = StaticAntarPurple,
    background = StaticAntarDark,
    onBackground = StaticAntarWhite,
    surface = StaticAntarSurface,
    onSurface = StaticAntarWhite,
    surfaceVariant = StaticAntarCard,
    onSurfaceVariant = StaticAntarGray,
    outline = StaticAntarDimGray,
    outlineVariant = Color(0xFF2A2F4A),
    error = AntarRed,
    onError = StaticAntarWhite,
    inverseSurface = StaticAntarWhite,
    inverseOnSurface = StaticAntarDark
)

private val AntarLightColorScheme = lightColorScheme(
    primary = Color(0xFF007A99),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF002025),
    secondary = Color(0xFF1976D2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = Color(0xFF6200EE),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF21005D),
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFF0F1F5),
    onSurfaceVariant = Color(0xFF5A5E6F),
    outline = Color(0xFF757780),
    outlineVariant = Color(0xFFD3D4DC),
    error = AntarRed,
    onError = Color.White,
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF2F0F4)
)

@Composable
fun ANTARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AntarColorScheme
        else -> AntarLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val currentBackground = colorScheme.background.toArgb()
            window.statusBarColor = currentBackground
            window.navigationBarColor = currentBackground
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AntarTypography,
        content = content
    )
}
