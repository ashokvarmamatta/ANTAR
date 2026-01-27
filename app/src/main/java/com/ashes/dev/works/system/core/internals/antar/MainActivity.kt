package com.ashes.dev.works.system.core.internals.antar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.presentation.screens.MainScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.ANTARTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ANTARTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}