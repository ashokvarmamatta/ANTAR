package com.ashes.dev.works.system.core.internals.antar.presentation.camera

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lens
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarOrange
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.BinningNotice
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.CameraCard
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.CameraSectionCard
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.ControlRows
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.ForensicsRows
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.LensRows
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.ModesRows
import com.ashes.dev.works.system.core.internals.antar.presentation.camera.components.ResolutionRows
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "cameraState"
    ) { state ->
        when (state) {
            CameraUiState.Loading -> LoadingSkeleton()
            CameraUiState.Empty -> ErrorState(
                message = R.string.camera_empty,
                icon = Icons.Outlined.CameraAlt,
                onRetry = viewModel::load
            )
            is CameraUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is CameraUiState.Content -> CameraContent(
                state = state,
                onSelect = viewModel::selectCamera,
                onRetrySelected = viewModel::retrySelected
            )
        }
    }
}

@Composable
private fun CameraContent(
    state: CameraUiState.Content,
    onSelect: (String) -> Unit,
    onRetrySelected: () -> Unit
) {
    val info = state.selectedInfo
    val selectedError = state.selectedError

    AdaptiveCardGrid {
        item(key = "selector", span = StaggeredGridItemSpan.FullLine) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredEntry(0),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.cameraIds, key = { it }) { id ->
                    CameraCard(
                        id = id,
                        info = state.infos[id],
                        isSelected = id == state.selectedId,
                        onClick = { onSelect(id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        item(key = "notice", span = StaggeredGridItemSpan.FullLine) {
            BinningNotice(modifier = Modifier.staggeredEntry(1))
        }

        when {
            info != null -> {
                item(key = "forensics") {
                    CameraSectionCard(info, 2, R.string.camera_section_forensics, Icons.Outlined.Info, AntarOrange) {
                        ForensicsRows(it)
                    }
                }
                item(key = "modes") {
                    CameraSectionCard(info, 3, R.string.camera_section_modes, Icons.Outlined.Tune, MaterialTheme.colorScheme.primary) {
                        ModesRows(it)
                    }
                }
                item(key = "control") {
                    CameraSectionCard(info, 4, R.string.camera_section_control, Icons.Outlined.Settings, AntarBlue) {
                        ControlRows(it)
                    }
                }
                item(key = "lens") {
                    CameraSectionCard(info, 5, R.string.camera_section_lens, Icons.Outlined.Lens, AntarPurple) {
                        LensRows(it)
                    }
                }
                item(key = "resolution") {
                    CameraSectionCard(info, 6, R.string.camera_section_resolution, Icons.Outlined.PhotoCamera, AntarGreen) {
                        ResolutionRows(it)
                    }
                }
            }

            selectedError != null -> item(key = "selectedError", span = StaggeredGridItemSpan.FullLine) {
                ErrorState(
                    message = selectedError,
                    onRetry = onRetrySelected,
                    modifier = Modifier
                        .staggeredEntry(2)
                        .animateItem()
                )
            }

            else -> item(key = "selectedLoading", span = StaggeredGridItemSpan.FullLine) {
                LoadingSkeleton(
                    sections = 2,
                    modifier = Modifier
                        .staggeredEntry(2)
                        .animateItem()
                )
            }
        }
    }
}
