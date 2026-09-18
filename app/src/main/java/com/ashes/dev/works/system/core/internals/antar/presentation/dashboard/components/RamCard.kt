package com.ashes.dev.works.system.core.internals.antar.presentation.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDimGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.domain.model.MemoryUsage
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatBytes
import com.ashes.dev.works.system.core.internals.antar.presentation.common.formatPercent
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientProgressBar

// ── RAM Card ─────────────────────────────────────────────────────────

@Composable
internal fun RamCard(ram: MemoryUsage, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = ram.usedFraction,
        animationSpec = LocalAnimationIntensity.current.effectsSpec(),
        label = "ram"
    )
    val trackColor = AntarDimGray.copy(alpha = 0.3f)
    // Theme-following colours are read in composition, not inside the Canvas draw lambda.
    val arcColors = listOf(AntarCyan, AntarBlue, AntarPurple)

    GradientHeaderCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    drawCircle(color = trackColor, radius = radius, style = Stroke(width = strokeWidth))
                    drawArc(
                        brush = Brush.sweepGradient(colors = arcColors),
                        startAngle = -90f,
                        sweepAngle = 360f * fraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth)
                    )
                }
                Text(
                    text = formatPercent(fraction),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AntarCyan
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_ram),
                    style = MaterialTheme.typography.labelMedium,
                    color = AntarCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = stringResource(R.string.dashboard_total, formatBytes(ram.totalBytes)),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientProgressBar(progress = fraction, colors = listOf(AntarCyan, AntarBlue))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_used, formatBytes(ram.usedBytes)),
                        style = MaterialTheme.typography.labelSmall,
                        color = AntarGray
                    )
                    Text(
                        text = stringResource(R.string.dashboard_free, formatBytes(ram.availableBytes)),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AntarCyan
                    )
                }
            }
        }
    }
}
