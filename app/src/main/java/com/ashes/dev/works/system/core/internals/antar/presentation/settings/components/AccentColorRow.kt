package com.ashes.dev.works.system.core.internals.antar.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarAccentColors
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick

@Composable
internal fun AccentColorRow(
    selectedIndex: Int,
    enabled: Boolean,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AntarAccentColors.forEachIndexed { index, color ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (enabled) 1f else 0.35f))
                    .border(
                        width = if (isSelected) 2.5.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    )
                    .then(if (enabled) Modifier.bounceClick { onSelected(index) } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.settings_accent_selected),
                        tint = AntarDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
