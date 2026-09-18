package com.ashes.dev.works.system.core.internals.antar.presentation.camera.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.CameraInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle

// ── Cards ────────────────────────────────────────────────────────────

/** A section card whose rows cross-fade when a different camera is selected. */
@Composable
internal fun CameraSectionCard(
    info: CameraInfo,
    index: Int,
    @StringRes title: Int,
    icon: ImageVector,
    accentColor: Color,
    rows: @Composable (CameraInfo) -> Unit
) {
    PremiumCard(modifier = Modifier.staggeredEntry(index)) {
        SectionTitle(title = title, icon = icon, accentColor = accentColor)
        AnimatedContent(
            targetState = info,
            contentKey = { it.id },
            transitionSpec = LocalAnimationIntensity.current.contentSwap(),
            label = "cameraSection"
        ) { shown ->
            Column(modifier = Modifier.fillMaxWidth()) {
                rows(shown)
            }
        }
    }
}
