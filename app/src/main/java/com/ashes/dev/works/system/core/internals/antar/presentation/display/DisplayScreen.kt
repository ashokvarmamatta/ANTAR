package com.ashes.dev.works.system.core.internals.antar.presentation.display

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.ui.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.ui.StatChip
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.BrightnessMode
import com.ashes.dev.works.system.core.internals.antar.domain.model.DisplayInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.ScreenOrientation
import org.koin.androidx.compose.koinViewModel

@Composable
fun DisplayScreen(viewModel: DisplayViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "displayState"
    ) { state ->
        when (state) {
            DisplayUiState.Loading -> LoadingSkeleton()
            is DisplayUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is DisplayUiState.Content -> DisplayContent(state.display)
        }
    }
}

@Composable
private fun DisplayContent(display: DisplayInfo) {
    val resolution = stringResource(R.string.display_value_resolution, display.widthPx, display.heightPx)
    val refresh = stringResource(R.string.display_value_refresh_rate, display.refreshRateHz)
    val diagonal = stringResource(R.string.display_value_diagonal, display.diagonalInches)
    val hdr = display.isHdr?.let { stringResource(if (it) R.string.common_supported else R.string.common_not_supported) }

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Monitor,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarBlue
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = resolution,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        display.name?.let { name ->
                            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = AntarCyan)
                        }
                        Text(
                            text = stringResource(R.string.display_value_size_and_refresh, diagonal, refresh),
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        item(key = "chips", span = StaggeredGridItemSpan.FullLine) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntry(1),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatChip(label = R.string.display_chip_refresh, value = refresh, accentColor = AntarCyan)
                }
                if (hdr != null) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatChip(label = R.string.display_chip_hdr, value = hdr, accentColor = AntarPurple)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatChip(label = R.string.display_chip_dpi, value = display.densityDpi.toString(), accentColor = AntarBlue)
                }
            }
        }

        item(key = "screen") {
            PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                SectionTitle(title = R.string.display_section_screen, icon = Icons.Outlined.Monitor)
                InfoRow(R.string.display_label_name, display.name)
                InfoRow(R.string.display_label_height, display.heightPx.toString())
                InfoRow(R.string.display_label_width, display.widthPx.toString())
                InfoRow(R.string.display_label_size, diagonal)
                InfoRow(
                    R.string.display_label_physical_size,
                    stringResource(R.string.display_value_physical_size, display.physicalWidthMm, display.physicalHeightMm)
                )
                InfoRow(
                    R.string.display_label_orientation,
                    stringResource(
                        when (display.orientation) {
                            ScreenOrientation.PORTRAIT -> R.string.display_orientation_portrait
                            ScreenOrientation.LANDSCAPE -> R.string.display_orientation_landscape
                        }
                    )
                )
                InfoRow(R.string.display_label_refresh_rate, refresh)
                InfoRow(R.string.display_label_hdr, hdr)
                InfoRow(
                    R.string.display_label_brightness_mode,
                    display.brightnessMode?.let { mode ->
                        stringResource(
                            when (mode) {
                                BrightnessMode.AUTOMATIC -> R.string.display_brightness_automatic
                                BrightnessMode.MANUAL -> R.string.display_brightness_manual
                            }
                        )
                    }
                )
                InfoRow(
                    R.string.display_label_screen_timeout,
                    display.screenTimeoutSeconds?.let { stringResource(R.string.display_value_seconds, it) }
                )
            }
        }

        item(key = "metrics") {
            PremiumCard(modifier = Modifier.staggeredEntry(3)) {
                SectionTitle(title = R.string.display_section_metrics, icon = Icons.Outlined.AspectRatio, accentColor = AntarPurple)
                InfoRow(R.string.display_label_density_bucket, display.densityBucket)
                InfoRow(R.string.display_label_density_dpi, display.densityDpi.toString())
                InfoRow(R.string.display_label_xdpi, stringResource(R.string.display_value_dpi_decimal, display.xdpi))
                InfoRow(R.string.display_label_ydpi, stringResource(R.string.display_value_dpi_decimal, display.ydpi))
                InfoRow(R.string.display_label_logical_density, display.density.toString())
                InfoRow(R.string.display_label_scaled_density, display.scaledDensity.toString())
                InfoRow(R.string.display_label_font_scale, display.fontScale.toString())
            }
        }
    }
}
