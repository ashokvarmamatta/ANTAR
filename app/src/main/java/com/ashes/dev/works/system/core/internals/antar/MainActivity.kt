package com.ashes.dev.works.system.core.internals.antar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ashes.dev.works.system.core.internals.antar.presentation.screens.MainScreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.ANTARTheme
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }

        enableEdgeToEdge()
        setContent {
            ANTARTheme {
                val navController = rememberNavController()
                var showExitDialog by remember { mutableStateOf(false) }
                val dashboardData by dashboardViewModel.dashboardInfo.collectAsState()

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

                if (dashboardData != null) {
                    MainScreen(navController = navController)
                } else {
                    LoadingSplash()
                }
            }
        }
    }
}

@Composable
fun LoadingSplash() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Antar",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "dots")
            
            val dotAlpha1 by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 600
                        0.2f at 0
                        1f at 200
                        0.2f at 400
                    }
                ), label = "dot1"
            )
            val dotAlpha2 by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 600
                        0.2f at 100
                        1f at 300
                        0.2f at 500
                    }
                ), label = "dot2"
            )
            val dotAlpha3 by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 600
                        0.2f at 200
                        1f at 400
                        0.2f at 600
                    }
                ), label = "dot3"
            )

            Dot(alpha = dotAlpha1)
            Dot(alpha = dotAlpha2)
            Dot(alpha = dotAlpha3)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading device information...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}
