package com.ashes.dev.works.system.core.internals.antar

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.app.AntarRoot
import com.ashes.dev.works.system.core.internals.antar.presentation.app.AppUiState
import com.ashes.dev.works.system.core.internals.antar.presentation.app.AppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/** Hosts [AntarRoot]: system splash, edge-to-edge window and system-bar styling only. */
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // The first frame waits for the first DataStore emission instead of blocking on it.
        splashScreen.setKeepOnScreenCondition { appViewModel.uiState.value is AppUiState.Loading }

        enableEdgeToEdge()
        setContent {
            AntarRoot(
                appViewModel = appViewModel,
                onDarkThemeChange = ::applySystemBarStyle,
                onExit = ::finish
            )
        }
    }

    /** Transparent bars with icon contrast that follows the app theme, not the system one. */
    private fun applySystemBarStyle(darkTheme: Boolean) {
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
    }

    private companion object {
        /** Same scrim enableEdgeToEdge uses by default for light 3-button navigation. */
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
    }
}
