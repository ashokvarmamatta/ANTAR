package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarMotion
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.presentation.main.MainScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.settings.SettingsScreen

const val MAIN_ROUTE = "main"

// Spring-driven slide for screen-to-screen motion so navigation flows instead of cutting.
private val slideSpring = spring<IntOffset>(
    dampingRatio = 0.85f,
    stiffness = Spring.StiffnessMediumLow
)

@Composable
fun NavGraph(navController: NavHostController) {
    // Shared-axis slide + fade; no transition at all on the low-motion / reduced-motion path.
    val animate = LocalAnimationIntensity.current != AnimationIntensity.LOW
    fun enter(offset: (Int) -> Int): EnterTransition =
        if (animate) fadeIn(tween(AntarMotion.MEDIUM_MS)) + slideInHorizontally(slideSpring, offset) else EnterTransition.None
    fun exit(offset: (Int) -> Int): ExitTransition =
        if (animate) fadeOut(tween(AntarMotion.FAST_MS)) + slideOutHorizontally(slideSpring, offset) else ExitTransition.None

    NavHost(navController = navController, startDestination = MAIN_ROUTE) {
        composable(
            route = MAIN_ROUTE,
            enterTransition = { enter { -it / 5 } },
            exitTransition = { exit { -it / 5 } },
            popEnterTransition = { enter { -it / 5 } },
            popExitTransition = { exit { -it / 5 } }
        ) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { enter { it / 2 } },
            exitTransition = { exit { it / 4 } },
            popEnterTransition = { enter { it / 4 } },
            popExitTransition = { exit { it / 2 } }
        ) {
            SettingsScreen(navController = navController)
        }
    }
}
