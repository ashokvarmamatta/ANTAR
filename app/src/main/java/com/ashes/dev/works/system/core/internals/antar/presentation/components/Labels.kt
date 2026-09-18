package com.ashes.dev.works.system.core.internals.antar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** A section heading: accent bar, optional icon, bold title. */
@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 12.dp)
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

object StatChipDefaults {
    val Shape: Shape = RoundedCornerShape(12.dp)
    val ContentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
}

/** A compact tinted chip: a bold [value] over a small [label]. */
@Composable
fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = StatChipDefaults.Shape
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .background(accentColor.copy(alpha = 0.08f))
            .border(0.5.dp, accentColor.copy(alpha = 0.2f), shape)
            .padding(StatChipDefaults.ContentPadding)
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
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/**
 * An inline label and value on one line. [label] is shown exactly as given, so the caller supplies
 * any localised separator (for example "Version: ").
 */
@Composable
fun LabelValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = label,
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
