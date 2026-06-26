package com.ashes.dev.works.system.core.internals.antar.presentation.navigation

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
import com.ashes.dev.works.system.core.internals.antar.presentation.screens.MainScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.screens.SettingsScreen

// Spring-driven slide for screen-to-screen motion so navigation flows instead of cutting.
private val slideSpring = spring<IntOffset>(
    dampingRatio = 0.85f,
    stiffness = Spring.StiffnessMediumLow
)

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
        composable(
            route = "main",
            enterTransition = { fadeIn(tween(250)) + slideInHorizontally(slideSpring) { -it / 5 } },
            exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(slideSpring) { -it / 5 } },
            popEnterTransition = { fadeIn(tween(250)) + slideInHorizontally(slideSpring) { -it / 5 } },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(slideSpring) { -it / 5 } }
        ) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(tween(250)) + slideInHorizontally(slideSpring) { it / 2 } },
            exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(slideSpring) { it / 4 } },
            popEnterTransition = { fadeIn(tween(250)) + slideInHorizontally(slideSpring) { it / 4 } },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(slideSpring) { it / 2 } }
        ) {
            SettingsScreen(navController = navController)
        }
    }
}
