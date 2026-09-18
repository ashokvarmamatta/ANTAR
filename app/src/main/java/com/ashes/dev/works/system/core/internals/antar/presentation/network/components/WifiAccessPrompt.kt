package com.ashes.dev.works.system.core.internals.antar.presentation.network.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.presentation.common.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.network.icon

/**
 * Inline stand-in for PermissionGate inside a card (the gate is a full-screen scrolling layout and
 * cannot sit in a lazy grid item). Same flow: explanation, in-app priming dialog before the system
 * dialog, and "Open settings" once Android will no longer ask.
 */
@Composable
internal fun WifiAccessPrompt(permanentlyDenied: Boolean, onRequestAccess: () -> Unit) {
    var showPriming by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Text(
        text = stringResource(if (permanentlyDenied) R.string.permission_denied_body else R.string.network_wifi_access_body),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))
    val interactionSource = remember { MutableInteractionSource() }
    FilledTonalButton(
        onClick = {
            if (permanentlyDenied) {
                openAppSettings(context)
            } else {
                showPriming = true
            }
        },
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interactionSource)
    ) {
        Text(
            text = stringResource(if (permanentlyDenied) R.string.permission_open_settings else R.string.permission_grant),
            fontWeight = FontWeight.Bold
        )
    }

    if (showPriming) {
        PermissionPrimingDialog(
            icon = Icons.Outlined.Security,
            title = R.string.network_wifi_priming_title,
            points = listOf(
                R.string.network_wifi_priming_point_purpose,
                R.string.network_wifi_priming_point_local,
                R.string.network_wifi_priming_point_private,
                R.string.network_wifi_priming_point_optional
            ),
            onAllow = {
                showPriming = false
                onRequestAccess()
            },
            onDismiss = { showPriming = false }
        )
    }
}

internal fun openAppSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
