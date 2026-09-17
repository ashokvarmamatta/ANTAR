package com.ashes.dev.works.system.core.internals.antar.core.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Shows [content] once [isGranted] holds; until then an explanation card. The system permission
 * dialog is only ever launched from the "Allow" button of the in-app priming dialog, never cold.
 * After a denial that Android will no longer re-ask for, the call to action becomes "Open settings"
 * instead of a button that silently does nothing.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGate(
    permissions: List<String>,
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes body: Int,
    @StringRes primingTitle: Int,
    primingPoints: List<Int>,
    modifier: Modifier = Modifier,
    isGranted: (MultiplePermissionsState) -> Boolean = { it.allPermissionsGranted },
    content: @Composable () -> Unit
) {
    var requestedOnce by rememberSaveable { mutableStateOf(false) }
    val state = rememberMultiplePermissionsState(permissions) { requestedOnce = true }
    var showPriming by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    if (isGranted(state)) {
        content()
        return
    }

    val permanentlyDenied = requestedOnce && !state.shouldShowRationale
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(if (permanentlyDenied) R.string.permission_denied_body else body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        val interactionSource = remember { MutableInteractionSource() }
        Button(
            onClick = {
                if (permanentlyDenied) {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    showPriming = true
                }
            },
            interactionSource = interactionSource,
            modifier = Modifier.pressScale(interactionSource),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(if (permanentlyDenied) R.string.permission_open_settings else R.string.permission_grant),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    if (showPriming) {
        PermissionPrimingDialog(
            icon = icon,
            title = primingTitle,
            points = primingPoints,
            onAllow = {
                showPriming = false
                state.launchMultiplePermissionRequest()
            },
            onDismiss = { showPriming = false }
        )
    }
}

/** The reusable priming dialog: icon disc, title, why, reassurance points, Allow and Not now. */
@Composable
fun PermissionPrimingDialog(
    icon: ImageVector,
    @StringRes title: Int,
    points: List<Int>,
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                points.forEach { point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(point),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                val allowSource = remember { MutableInteractionSource() }
                Button(
                    onClick = onAllow,
                    interactionSource = allowSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(allowSource),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.permission_allow), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                val laterSource = remember { MutableInteractionSource() }
                TextButton(
                    onClick = onDismiss,
                    interactionSource = laterSource,
                    modifier = Modifier.pressScale(laterSource)
                ) {
                    Text(stringResource(R.string.permission_not_now), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
