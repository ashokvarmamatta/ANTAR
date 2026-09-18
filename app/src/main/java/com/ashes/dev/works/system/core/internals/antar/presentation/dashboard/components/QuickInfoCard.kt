package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray

// ── Quick Info Card ──────────────────────────────────────────────────

@Composable
internal fun QuickInfoCard(
    @StringRes title: Int,
    value: String,
    subtitle: String?,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(accentColor.copy(alpha = 0.06f))
            .border(0.5.dp, accentColor.copy(alpha = 0.15f), shape)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = accentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.labelSmall,
                color = AntarGray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle.orEmpty(),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = AntarGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
