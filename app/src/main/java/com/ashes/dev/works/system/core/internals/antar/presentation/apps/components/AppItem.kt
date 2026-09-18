package com.ashes.dev.works.system.core.internals.antar.presentation.apps.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.spatialSpec
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.AppIconLoader
import com.ashes.dev.works.system.core.internals.antar.presentation.common.LabelValue
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LabelValue
import org.koin.compose.koinInject

@Composable
internal fun AppItem(
    app: AppDetail,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconLoader: AppIconLoader = koinInject()
) {
    val context = LocalContext.current
    val iconSizePx = with(LocalDensity.current) { 44.dp.roundToPx() }
    var iconBitmap by remember(app.packageName) {
        mutableStateOf<ImageBitmap?>(iconLoader.cached(app.packageName, iconSizePx))
    }
    LaunchedEffect(app.packageName, iconSizePx) {
        if (iconBitmap == null) iconBitmap = iconLoader.load(app.packageName, iconSizePx)
    }
    val cannotOpen = stringResource(R.string.apps_cannot_open)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape)
            .animateContentSize(animationSpec = LocalAnimationIntensity.current.spatialSpec())
            .bounceClick(pressedScale = 0.98f) { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    iconBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = AntarGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabelValue(R.string.apps_label_version, app.version ?: NO_VALUE, Modifier.weight(1f, fill = false))
                        LabelValue(R.string.apps_label_api, app.targetSdk.toString(), Modifier.weight(1f, fill = false))
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val infoSource = remember { MutableInteractionSource() }
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", app.packageName, null))
                            )
                        },
                        interactionSource = infoSource,
                        modifier = Modifier.pressScale(infoSource)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.apps_action_app_info))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val openSource = remember { MutableInteractionSource() }
                    Button(
                        onClick = {
                            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (intent != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, cannotOpen, Toast.LENGTH_SHORT).show()
                            }
                        },
                        interactionSource = openSource,
                        modifier = Modifier.pressScale(openSource),
                        colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.apps_action_open), fontWeight = FontWeight.Bold, color = AntarDark)
                    }
                }
            }
        }
    }
}
