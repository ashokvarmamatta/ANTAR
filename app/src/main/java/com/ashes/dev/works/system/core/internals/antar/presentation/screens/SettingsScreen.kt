package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarRed

private const val PRIVACY_POLICY_URL = "https://ashokvarma.dev/antar/privacy"
private const val DATA_DELETION_URL = "https://ashokvarma.dev/antar/data-deletion"
private const val DEVELOPER_URL = "https://ashokvarma.dev"

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val packageName = context.packageName
    val versionName = remember(packageName) { readVersionName(context) }

    Scaffold(
        topBar = {
            Surface(color = AntarDark, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = AntarCyan
                        )
                    }
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AntarCyan,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GradientHeaderCard {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "ANTAR",
                            style = MaterialTheme.typography.labelMedium,
                            color = AntarCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version $versionName",
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Support the app", icon = Icons.Outlined.Star)
                    SettingsRow(
                        icon = Icons.Outlined.Star,
                        accent = AntarCyan,
                        title = "Rate this app",
                        subtitle = "Open Google Play to leave a review",
                        onClick = { openPlayStore(context, packageName) }
                    )
                    SettingsRow(
                        icon = Icons.Outlined.Share,
                        accent = AntarPurple,
                        title = "Share with a friend",
                        subtitle = "Send the Play Store link",
                        onClick = { shareApp(context, packageName) }
                    )
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "Privacy & data", icon = Icons.Outlined.PrivacyTip, accentColor = AntarPurple)
                    SettingsRow(
                        icon = Icons.Outlined.PrivacyTip,
                        accent = AntarPurple,
                        title = "Privacy policy",
                        subtitle = PRIVACY_POLICY_URL.removePrefix("https://"),
                        onClick = { openUrl(context, PRIVACY_POLICY_URL) }
                    )
                    SettingsRow(
                        icon = Icons.Outlined.DeleteForever,
                        accent = AntarRed,
                        title = "Request data deletion",
                        subtitle = "ANTAR stores nothing off-device — open app info to clear local data",
                        onClick = { openAppInfoSettings(context) }
                    )
                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        accent = AntarCyan,
                        title = "What ANTAR collects",
                        subtitle = "Read the data-deletion page",
                        onClick = { openUrl(context, DATA_DELETION_URL) }
                    )
                }
            }

            item {
                PremiumCard {
                    SectionTitle(title = "About", icon = Icons.Outlined.Info)
                    InfoRow("App name", stringResource(id = R.string.app_name))
                    InfoRow("Package", packageName)
                    InfoRow("Version", versionName)
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        accent = AntarCyan,
                        title = "Developer",
                        subtitle = DEVELOPER_URL.removePrefix("https://"),
                        onClick = { openUrl(context, DEVELOPER_URL) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = AntarGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun openPlayStore(context: Context, packageName: String) {
    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=$packageName")
    }
}

private fun shareApp(context: Context, packageName: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out ANTAR — a clean device-info app for Android.\n" +
                    "https://play.google.com/store/apps/details?id=$packageName"
        )
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share ANTAR via"))
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
    }
}

private fun openAppInfoSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "Can't open app settings", Toast.LENGTH_SHORT).show()
    }
}

private fun readVersionName(context: Context): String {
    return try {
        val pm = context.packageManager
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(context.packageName, 0)
        }
        info.versionName ?: "1.0"
    } catch (_: Exception) {
        "1.0"
    }
}
