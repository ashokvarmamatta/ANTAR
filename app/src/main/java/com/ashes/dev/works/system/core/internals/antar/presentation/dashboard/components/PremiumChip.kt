package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan

// ── Chip ─────────────────────────────────────────────────────────────

@Composable
internal fun PremiumChip(text: String, accent: Boolean = false) {
    val chipColor = if (accent) AntarCyan.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = if (accent) AntarCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val textColor = if (accent) AntarCyan else MaterialTheme.colorScheme.onSurface

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = chipColor,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
