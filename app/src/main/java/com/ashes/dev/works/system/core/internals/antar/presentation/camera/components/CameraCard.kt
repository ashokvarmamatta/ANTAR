package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo

@Composable
internal fun CameraCard(
    id: String,
    info: CameraInfo?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val intensity = LocalAnimationIntensity.current
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AntarCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = intensity.effectsSpec(),
        label = "cameraCardBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AntarCyan.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        animationSpec = intensity.effectsSpec(),
        label = "cameraCardBorder"
    )
    val accentColor = if (isSelected) AntarCyan else AntarGray
    val shape = RoundedCornerShape(16.dp)

    val cameraLabel = stringResource(R.string.camera_card_camera_id, id)
    val megapixels = info?.megapixels?.let { stringResource(R.string.camera_value_megapixels, it) }
    val resolution = info?.maxJpegSize?.let { sizeText(it) }
    val title = megapixels ?: cameraLabel
    val subtitle = when {
        isSelected -> resolution.orEmpty()
        megapixels != null -> cameraLabel
        else -> ""
    }
    val facing = info?.lensFacing?.let { stringResource(facingRes(it)) }.orEmpty()

    Box(
        modifier = modifier
            .width(140.dp)
            .height(120.dp)
            .semantics { selected = isSelected }
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .bounceClick(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) AntarCyan else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AntarGray,
                    maxLines = 1
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = facing,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = stringResource(R.string.camera_cd_selected),
                        modifier = Modifier.size(18.dp),
                        tint = AntarCyan
                    )
                }
            }
        }
    }
}
