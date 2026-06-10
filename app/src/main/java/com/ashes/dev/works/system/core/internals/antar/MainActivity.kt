package com.ashes.dev.works.system.core.internals.antar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.data.preference.ThemePreferences
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AnimatedSplash
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ExitDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.NavGraph
import com.ashes.dev.works.system.core.internals.antar.presentation.screens.intro.IntroScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.ANTARTheme
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private enum class RootState { Intro, Splash, Main }

class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModel()
    private val themeViewModel: ThemeViewModel by viewModel()
    private val themePreferences: ThemePreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }

        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val dynamicColors by themeViewModel.dynamicColorsEnabled.collectAsState()

            val darkTheme = when (themeMode) {
                ThemePreferences.MODE_LIGHT -> false
                ThemePreferences.MODE_DARK -> true
                else -> isSystemInDarkTheme()
            }

            ANTARTheme(darkTheme = darkTheme, dynamicColor = dynamicColors) {
                val navController = rememberNavController()
                var showExitDialog by remember { mutableStateOf(false) }
                var introSeen by remember { mutableStateOf(themePreferences.introSeen) }
                val dashboardData by dashboardViewModel.dashboardInfo.collectAsState()

                // Keep the splash visible for at least 3s once it appears (after the
                // intro, or immediately on a normal launch), even if data loads sooner.
                var splashMinTimeElapsed by remember { mutableStateOf(false) }
                LaunchedEffect(introSeen) {
                    if (introSeen) {
                        delay(3000)
                        splashMinTimeElapsed = true
                    }
                }

                val rootState = when {
                    !introSeen -> RootState.Intro
                    dashboardData == null || !splashMinTimeElapsed -> RootState.Splash
                    else -> RootState.Main
                }

                if (showExitDialog) {
                    ExitDialog(
                        onDismiss = { showExitDialog = false },
                        onConfirm = { finish() }
                    )
                }

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val isOnMain = currentBackStackEntry?.destination?.route.let { it == null || it == "main" }

                BackHandler(enabled = rootState == RootState.Main && isOnMain) {
                    showExitDialog = true
                }

                Crossfade(
                    targetState = rootState,
                    animationSpec = tween(450),
                    label = "root"
                ) { state ->
                    when (state) {
                        RootState.Intro -> IntroScreen(
                            onFinish = {
                                themePreferences.introSeen = true
                                introSeen = true
                            }
                        )
                        RootState.Splash -> AnimatedSplash()
                        RootState.Main -> NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
