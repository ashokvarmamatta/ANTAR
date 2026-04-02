package com.ashes.dev.works.system.core.internals.antar.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AntarColorScheme = darkColorScheme(
    primary = AntarCyan,
    onPrimary = AntarDark,
    primaryContainer = AntarTeal,
    onPrimaryContainer = AntarWhite,
    secondary = AntarBlue,
    onSecondary = AntarWhite,
    secondaryContainer = AntarSurfaceElevated,
    onSecondaryContainer = AntarCyan,
    tertiary = AntarPurple,
    onTertiary = AntarDark,
    tertiaryContainer = Color(0xFF2D1F5E),
    onTertiaryContainer = AntarPurple,
    background = AntarDark,
    onBackground = AntarWhite,
    surface = AntarSurface,
    onSurface = AntarWhite,
    surfaceVariant = AntarCard,
    onSurfaceVariant = AntarGray,
    outline = AntarDimGray,
    outlineVariant = Color(0xFF2A2F4A),
    error = AntarRed,
    onError = AntarWhite,
    inverseSurface = AntarWhite,
    inverseOnSurface = AntarDark
)

@Composable
fun ANTARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        else -> AntarColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AntarDark.toArgb()
            window.navigationBarColor = AntarDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AntarTypography,
        content = content
    )
}
