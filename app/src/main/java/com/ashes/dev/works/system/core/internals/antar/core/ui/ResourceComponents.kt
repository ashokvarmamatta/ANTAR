package com.ashes.dev.works.system.core.internals.antar.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.CopyableInfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.ErrorStateDefaults
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LabelValue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PermissionGate
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PermissionGateText
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PermissionPrimingDialog
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PermissionPrimingText
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.StatChip
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

/*
 * ANTAR's bindings for the shared component library in core/designsystem/component.
 * The library takes plain strings so it can be copied into any app; these overloads resolve
 * ANTAR's string resources (and its shared labels such as Retry or Copied) at the call site.
 */

// motion:allow bindings only: press feedback lives in the library components they delegate to

@Composable
fun InfoRow(@StringRes label: Int, value: String?, modifier: Modifier = Modifier, singleLine: Boolean = true) {
    InfoRow(stringResource(label), value, modifier, singleLine, missingValue = NO_VALUE)
}

@Composable
fun CopyableInfoRow(@StringRes label: Int, value: String?, modifier: Modifier = Modifier) {
    val labelText = stringResource(label)
    CopyableInfoRow(
        label = labelText,
        value = value,
        copyContentDescription = stringResource(R.string.common_copy_value, labelText),
        modifier = modifier,
        copiedMessage = stringResource(R.string.common_copied, labelText),
        missingValue = NO_VALUE
    )
}

@Composable
fun SectionTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color? = null
) {
    if (accentColor == null) {
        SectionTitle(stringResource(title), modifier, icon)
    } else {
        SectionTitle(stringResource(title), modifier, icon, accentColor)
    }
}

@Composable
fun StatChip(@StringRes label: Int, value: String, modifier: Modifier = Modifier, accentColor: Color? = null) {
    if (accentColor == null) {
        StatChip(stringResource(label), value, modifier)
    } else {
        StatChip(stringResource(label), value, modifier, accentColor)
    }
}

/** Renders "Label: value" with the localised separator from `common_label_value_prefix`. */
@Composable
fun LabelValue(@StringRes label: Int, value: String, modifier: Modifier = Modifier) {
    LabelValue(stringResource(R.string.common_label_value_prefix, stringResource(label)), value, modifier)
}

/** Full-screen error for a string resource, with ANTAR's Retry button when [onRetry] is given. */
@Composable
fun ErrorState(
    @StringRes message: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector = ErrorStateDefaults.Icon,
    onRetry: (() -> Unit)? = null
) {
    ErrorState(
        message = stringResource(message),
        modifier = modifier,
        icon = icon,
        action = onRetry?.let { retry ->
            { ErrorStateDefaults.RetryButton(label = stringResource(R.string.common_retry), onClick = retry) }
        }
    )
}

@Composable
fun primingText(@StringRes title: Int, points: List<Int>): PermissionPrimingText = PermissionPrimingText(
    title = stringResource(title),
    points = points.map { stringResource(it) },
    allowLabel = stringResource(R.string.permission_allow),
    notNowLabel = stringResource(R.string.permission_not_now)
)

@Composable
fun PermissionPrimingDialog(
    icon: ImageVector,
    @StringRes title: Int,
    points: List<Int>,
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    PermissionPrimingDialog(
        icon = icon,
        text = primingText(title, points),
        onAllow = onAllow,
        onDismiss = onDismiss
    )
}

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
    PermissionGate(
        permissions = permissions,
        icon = icon,
        text = PermissionGateText(
            title = stringResource(title),
            body = stringResource(body),
            deniedBody = stringResource(R.string.permission_denied_body),
            grantLabel = stringResource(R.string.permission_grant),
            openSettingsLabel = stringResource(R.string.permission_open_settings),
            priming = primingText(primingTitle, primingPoints)
        ),
        modifier = modifier,
        isGranted = isGranted,
        content = content
    )
}
