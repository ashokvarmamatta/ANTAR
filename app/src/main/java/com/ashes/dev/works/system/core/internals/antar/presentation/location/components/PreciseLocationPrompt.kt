package com.ashes.dev.works.system.core.internals.antar.presentation.location.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
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
import com.ashes.dev.works.system.core.internals.antar.presentation.common.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.presentation.common.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.presentation.location.LOCATION_PRIMING_POINTS

/**
 * Shown on approximate-only access. Same flow as PermissionGate: in-app priming before the system
 * dialog, and "Open settings" once Android will no longer ask.
 */
@Composable
internal fun PreciseLocationPrompt(permanentlyDenied: Boolean, onRequestAccess: () -> Unit) {
    var showPriming by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Text(
        text = stringResource(R.string.location_precise_needed_title),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = stringResource(if (permanentlyDenied) R.string.permission_denied_body else R.string.location_precise_needed_body),
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
            text = stringResource(if (permanentlyDenied) R.string.permission_open_settings else R.string.location_precise_action),
            fontWeight = FontWeight.Bold
        )
    }

    if (showPriming) {
        PermissionPrimingDialog(
            icon = Icons.Outlined.GpsFixed,
            title = R.string.location_priming_title,
            points = LOCATION_PRIMING_POINTS,
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
