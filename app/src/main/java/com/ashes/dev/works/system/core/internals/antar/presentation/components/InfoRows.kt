package com.ashes.dev.works.system.core.internals.antar.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale

object InfoRowDefaults {
    /** Placeholder a data source uses for "could not be read"; rows showing it are hidden. */
    const val MissingValue: String = "- - -"

    /** Values longer than this switch the row to the stacked label-over-value layout. */
    const val LongValueThreshold: Int = 30

    val ContentPadding: PaddingValues = PaddingValues(vertical = 6.dp)

    /** True when [value] has nothing worth showing. */
    fun isMissing(value: String?, missingValue: String? = MissingValue): Boolean =
        value.isNullOrBlank() || value == missingValue
}

/**
 * A label/value row. Short values sit on one line (label at the start, value at the end); long
 * values, or [singleLine] = false, stack the value under the label. A row with no readable value
 * (null, blank or [missingValue]) renders nothing, so a card never shows an invented value.
 */
@Composable
fun InfoRow(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    missingValue: String? = InfoRowDefaults.MissingValue
) {
    if (value == null || InfoRowDefaults.isMissing(value, missingValue)) return

    if (singleLine && value.length <= InfoRowDefaults.LongValueThreshold) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(InfoRowDefaults.ContentPadding),
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
            modifier = modifier
                .fillMaxWidth()
                .padding(InfoRowDefaults.ContentPadding)
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

object CopyableInfoRowDefaults {
    val CopyIcon: ImageVector = Icons.Outlined.ContentCopy

    /** Copies [value] to the system clipboard and, when [confirmation] is given, shows it as a toast. */
    fun copyToClipboard(context: Context, label: String, value: String, confirmation: String?) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
        if (confirmation != null) Toast.makeText(context, confirmation, Toast.LENGTH_SHORT).show()
    }
}

/**
 * A label/value row with a copy button. [copyContentDescription] labels the button for
 * accessibility. [onCopy] replaces the default behaviour: clipboard plus a [copiedMessage] toast.
 */
@Composable
fun CopyableInfoRow(
    label: String,
    value: String?,
    copyContentDescription: String,
    modifier: Modifier = Modifier,
    copiedMessage: String? = null,
    missingValue: String? = InfoRowDefaults.MissingValue,
    onCopy: ((String) -> Unit)? = null
) {
    if (value == null || InfoRowDefaults.isMissing(value, missingValue)) return

    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
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
                if (onCopy != null) {
                    onCopy(value)
                } else {
                    CopyableInfoRowDefaults.copyToClipboard(context, label, value, copiedMessage)
                }
            },
            interactionSource = interactionSource,
            modifier = Modifier.pressScale(interactionSource)
        ) {
            Icon(
                imageVector = CopyableInfoRowDefaults.CopyIcon,
                contentDescription = copyContentDescription,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
