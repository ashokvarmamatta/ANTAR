package com.ashes.dev.works.system.core.internals.antar.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast

/** Intents the Settings screen fires: links, Play Store rating, app info. */

internal const val PRIVACY_POLICY_URL = "https://ashes-dev-works.web.app/antar/"
internal const val DEVELOPER_URL = "https://ashokvarma.dev"
internal const val PLAY_PACKAGE = "com.ashes.dev.works.system.core.internals.antar"

internal fun openUrl(context: Context, url: String, noBrowserMessage: String? = null) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        if (noBrowserMessage != null) Toast.makeText(context, noBrowserMessage, Toast.LENGTH_SHORT).show()
    }
}

internal fun rateApp(context: Context, noBrowserMessage: String) {
    // The base package name is used explicitly: debug builds carry a ".debug" suffix that does not
    // exist on the Play Store.
    val goToMarket = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PLAY_PACKAGE")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        context.startActivity(goToMarket)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=$PLAY_PACKAGE", noBrowserMessage)
    }
}

internal fun openAppInfoSettings(context: Context, failureMessage: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
    }
}

internal fun readVersionName(context: Context): String = try {
    val pm = context.packageManager
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        pm.getPackageInfo(context.packageName, 0)
    }
    info.versionName.orEmpty()
} catch (_: Exception) {
    ""
}
