package com.ashes.dev.works.system.core.internals.antar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                var showExitDialog by remember { mutableStateOf(false) }

                if (showExitDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitDialog = false },
                        title = { Text(text = "Exit App") },
                        text = { Text(text = "Are you sure you want to exit?") },
                        confirmButton = {
                            TextButton(onClick = { finish() }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExitDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                BackHandler(enabled = true) {
                    showExitDialog = true
                }

                MainScreen(navController = navController)
            }
        }
    }
}
