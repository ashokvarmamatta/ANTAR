package com.ashes.dev.works.system.core.internals.antar.core.designsystem.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.GradientEnd
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.GradientMid
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.GradientStart
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.shimmer
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.spatialSpec

// ── Info Row ─────────────────────────────────────────────────────────

/** A label/value row. Rows whose value could not be read (null, blank or [NO_VALUE]) are hidden. */
@Composable
fun InfoRow(@StringRes label: Int, value: String?, singleLine: Boolean = true) {
    InfoRow(label = stringResource(label), value = value, singleLine = singleLine)
}

/** Variant for labels that come from the device itself (e.g. keys of `/proc/cpuinfo`). */
@Composable
fun InfoRow(label: String, value: String?, singleLine: Boolean = true) {
    if (value.isNullOrBlank() || value == NO_VALUE) return

    val isLongValue = value.length > 30 || !singleLine

    if (!isLongValue) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.45f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.55f),
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ── Copyable Info Row ────────────────────────────────────────────────

@Composable
fun CopyableInfoRow(@StringRes label: Int, value: String?) {
    if (value.isNullOrBlank() || value == NO_VALUE) return

    val context = LocalContext.current
    val labelText = stringResource(label)
    val copiedText = stringResource(R.string.common_copied, labelText)
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        // Default IconButton size keeps the 48dp touch target; only the glyph is small.
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(labelText, value))
                Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
            },
            interactionSource = interactionSource,
            modifier = Modifier.pressScale(interactionSource)
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = stringResource(R.string.common_copy_value, labelText),
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Gradient Header Card ─────────────────────────────────────────────

@Composable
fun GradientHeaderCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = 0.15f),
                        GradientMid.copy(alpha = 0.10f),
                        GradientEnd.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = 0.4f),
                        GradientEnd.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

// ── Section Card ─────────────────────────────────────────────────────

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = shape
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .animateContentSize(animationSpec = LocalAnimationIntensity.current.spatialSpec())
            ) {
                content()
            }
        }
    }
}

// ── Section Title ────────────────────────────────────────────────────

@Composable
fun SectionTitle(
    @StringRes title: Int,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    SectionTitle(title = stringResource(title), icon = icon, accentColor = accentColor)
}

@Composable
fun SectionTitle(
    title: String,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Stat Chip ────────────────────────────────────────────────────────

@Composable
fun StatChip(
    @StringRes label: Int,
    value: String,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape)
            .background(accentColor.copy(alpha = 0.08f))
            .border(0.5.dp, accentColor.copy(alpha = 0.2f), shape)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

// ── Label Value pair (inline) ────────────────────────────────────────

@Composable
fun LabelValue(@StringRes label: Int, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.common_label_value_prefix, stringResource(label)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ── Progress Bar ─────────────────────────────────────────────────────

@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    colors: List<Color> = listOf(AntarCyan, MaterialTheme.colorScheme.secondary)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(Brush.horizontalGradient(colors))
        )
    }
}

// ── Loading & error states ───────────────────────────────────────────

/**
 * Shimmering placeholder cards in the shape of an info screen: a header card and [sections]
 * section cards. Used for every content-shaped load instead of a bare spinner.
 */
@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier, sections: Int = 3) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmer()
        )
        repeat(sections) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmer()
            )
        }
    }
}

/** Full-screen error with an optional retry. [message] is a string resource, never raw exception text. */
@Composable
fun ErrorState(
    @StringRes message: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.ErrorOutline,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = AntarRed
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(20.dp))
            val interactionSource = remember { MutableInteractionSource() }
            Button(
                onClick = onRetry,
                interactionSource = interactionSource,
                modifier = Modifier.pressScale(interactionSource),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = stringResource(R.string.common_retry), color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
