package com.ashes.dev.works.system.core.internals.antar

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.ANTARTheme
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarAccentColors
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.rememberReducedMotion
import com.ashes.dev.works.system.core.internals.antar.core.ui.AnimatedSplash
import com.ashes.dev.works.system.core.internals.antar.core.ui.ExitDialog
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import com.ashes.dev.works.system.core.internals.antar.presentation.app.AppUiState
import com.ashes.dev.works.system.core.internals.antar.presentation.app.AppViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.DashboardViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.isReady
import com.ashes.dev.works.system.core.internals.antar.presentation.intro.IntroScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.MAIN_ROUTE
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.NavGraph
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel

private enum class RootState { Intro, Splash, Main }

/** Minimum time the animated brand splash stays up once it appears. */
private const val SPLASH_MIN_MS = 3000L

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModel()
    private val dashboardViewModel: DashboardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // The first frame waits for the first DataStore emission instead of blocking on it.
        splashScreen.setKeepOnScreenCondition { appViewModel.uiState.value is AppUiState.Loading }

        enableEdgeToEdge()
        setContent {
            val appState by appViewModel.uiState.collectAsStateWithLifecycle()
            val settings = (appState as? AppUiState.Ready)?.settings ?: return@setContent

            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            // Transparent bars with icon contrast that follows the app theme, not the system one.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, LIGHT_SCRIM)
                    }
                )
                onDispose {}
            }

            val reducedMotion = rememberReducedMotion()
            val intensity = when {
                reducedMotion -> AnimationIntensity.LOW
                settings.motionLevel == MotionLevel.LOW -> AnimationIntensity.LOW
                settings.motionLevel == MotionLevel.MEDIUM -> AnimationIntensity.MEDIUM
                else -> AnimationIntensity.HIGH
            }
            val accentColor = AntarAccentColors.getOrElse(settings.accentIndex) { AntarAccentColors[0] }

            ANTARTheme(darkTheme = darkTheme, dynamicColor = settings.dynamicColors, accentColor = accentColor) {
                val navController = rememberNavController()
                var showExitDialog by rememberSaveable { mutableStateOf(false) }
                val dashboardData by dashboardViewModel.uiState.collectAsStateWithLifecycle()

                // Keep the splash visible for a minimum time once it appears (after the intro,
                // or immediately on a normal launch), even if data loads sooner.
                var splashMinTimeElapsed by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(settings.introSeen) {
                    if (settings.introSeen && !splashMinTimeElapsed) {
                        delay(SPLASH_MIN_MS)
                        splashMinTimeElapsed = true
                    }
                }

                val rootState = when {
                    !settings.introSeen -> RootState.Intro
                    !dashboardData.isReady || !splashMinTimeElapsed -> RootState.Splash
                    else -> RootState.Main
                }

                if (showExitDialog) {
                    ExitDialog(
                        onDismiss = { showExitDialog = false },
                        onConfirm = { finish() }
                    )
                }

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val isOnMain = currentBackStackEntry?.destination?.route.let { it == null || it == MAIN_ROUTE }

                BackHandler(enabled = rootState == RootState.Main && isOnMain) {
                    showExitDialog = true
                }

                CompositionLocalProvider(LocalAnimationIntensity provides intensity) {
                    Crossfade(
                        targetState = rootState,
                        animationSpec = tween(if (intensity == AnimationIntensity.LOW) 0 else AntarMotion.MEDIUM_MS),
                        label = "root"
                    ) { state ->
                        when (state) {
                            RootState.Intro -> IntroScreen(onFinish = appViewModel::finishIntro)
                            RootState.Splash -> AnimatedSplash()
                            RootState.Main -> NavGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        /** Same scrim enableEdgeToEdge uses by default for light 3-button navigation. */
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
    }
}
