package com.ashes.dev.works.system.core.internals.antar.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.ANTARTheme
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarAccentColors
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.rememberReducedMotion
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppSettings
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import com.ashes.dev.works.system.core.internals.antar.presentation.app.components.ExitDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.DashboardViewModel
import com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.isReady
import com.ashes.dev.works.system.core.internals.antar.presentation.intro.IntroScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.MAIN_ROUTE
import com.ashes.dev.works.system.core.internals.antar.presentation.navigation.NavGraph
import com.ashes.dev.works.system.core.internals.antar.presentation.splash.AnimatedSplash
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private enum class RootState { Intro, Splash, Main }

/** Minimum time the animated brand splash stays up once it appears. */
private const val SPLASH_MIN_MS = 3000L

/**
 * Everything the app shows, from the first frame: applies the user's theme and motion settings,
 * then moves between onboarding, the animated splash and the tab screens. The activity only hosts
 * it; [onDarkThemeChange] lets it restyle the system bars and [onExit] closes the app.
 */
@Composable
fun AntarRoot(
    appViewModel: AppViewModel,
    onDarkThemeChange: (Boolean) -> Unit,
    onExit: () -> Unit
) {
    val appState by appViewModel.uiState.collectAsStateWithLifecycle()
    val settings = (appState as? AppUiState.Ready)?.settings ?: return

    val darkTheme = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    LaunchedEffect(darkTheme) { onDarkThemeChange(darkTheme) }

    val intensity = motionIntensity(settings, rememberReducedMotion())
    val accentColor = AntarAccentColors.getOrElse(settings.accentIndex) { AntarAccentColors[0] }

    ANTARTheme(darkTheme = darkTheme, dynamicColor = settings.dynamicColors, accentColor = accentColor) {
        CompositionLocalProvider(LocalAnimationIntensity provides intensity) {
            RootContent(
                introSeen = settings.introSeen,
                intensity = intensity,
                onFinishIntro = appViewModel::finishIntro,
                onExit = onExit
            )
        }
    }
}

/** Onboarding until it is finished, then the splash until the dashboard has data, then the tabs. */
@Composable
private fun RootContent(
    introSeen: Boolean,
    intensity: AnimationIntensity,
    onFinishIntro: () -> Unit,
    onExit: () -> Unit
) {
    // Activity-scoped (outside the NavHost): the splash stays until this first dashboard load is ready.
    val dashboardViewModel: DashboardViewModel = koinViewModel()
    val dashboardData by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    // Keep the splash visible for a minimum time once it appears (after the intro, or immediately
    // on a normal launch), even if data loads sooner.
    var splashMinTimeElapsed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(introSeen) {
        if (introSeen && !splashMinTimeElapsed) {
            delay(SPLASH_MIN_MS)
            splashMinTimeElapsed = true
        }
    }

    val rootState = when {
        !introSeen -> RootState.Intro
        !dashboardData.isReady || !splashMinTimeElapsed -> RootState.Splash
        else -> RootState.Main
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isOnMain = currentBackStackEntry?.destination?.route.let { it == null || it == MAIN_ROUTE }
    BackHandler(enabled = rootState == RootState.Main && isOnMain) { showExitDialog = true }

    if (showExitDialog) {
        ExitDialog(onDismiss = { showExitDialog = false }, onConfirm = onExit)
    }

    Crossfade(
        targetState = rootState,
        animationSpec = tween(if (intensity == AnimationIntensity.LOW) 0 else AntarMotion.MEDIUM_MS),
        label = "root"
    ) { state ->
        when (state) {
            RootState.Intro -> IntroScreen(onFinish = onFinishIntro)
            RootState.Splash -> AnimatedSplash()
            RootState.Main -> NavGraph(navController = navController)
        }
    }
}

/** The system "remove animations" setting always wins over the in-app motion level. */
private fun motionIntensity(settings: AppSettings, reducedMotion: Boolean): AnimationIntensity = when {
    reducedMotion -> AnimationIntensity.LOW
    settings.motionLevel == MotionLevel.LOW -> AnimationIntensity.LOW
    settings.motionLevel == MotionLevel.MEDIUM -> AnimationIntensity.MEDIUM
    else -> AnimationIntensity.HIGH
}
