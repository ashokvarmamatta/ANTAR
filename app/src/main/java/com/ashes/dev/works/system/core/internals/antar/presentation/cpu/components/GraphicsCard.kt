package com.ashes.dev.works.system.core.internals.antar.presentation.cpu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.domain.model.GpuInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.cpu.mhzText

@Composable
internal fun GraphicsCard(gpu: GpuInfo, modifier: Modifier = Modifier) {
    // The extension list scrolls inside the card; whatever it does not use stays here instead of
    // dragging the whole page, so reaching the end of the list does not scroll the screen.
    val keepScrollInside = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
                if (source == NestedScrollSource.UserInput) available else Offset.Zero
        }
    }

    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.cpu_section_graphics, icon = Icons.Outlined.DeveloperBoard, accentColor = AntarGreen)
        InfoRow(R.string.cpu_label_gpu_renderer, gpu.renderer)
        InfoRow(R.string.cpu_label_gpu_vendor, gpu.vendor)
        InfoRow(R.string.cpu_label_opengl_es, gpu.openGlEsVersion)
        gpu.openGlExtensions?.let { extensions ->
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .nestedScroll(keepScrollInside)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = extensions,
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
            }
        }
        InfoRow(
            R.string.cpu_label_vulkan,
            gpu.vulkanHardwareLevel
                ?.let { stringResource(R.string.cpu_value_vulkan_level, it) }
                ?: stringResource(R.string.common_not_supported)
        )
        InfoRow(R.string.cpu_label_gpu_frequency, gpu.maxFrequencyHz?.let { mhzText(it / HZ_PER_MHZ) })
        InfoRow(R.string.cpu_label_gpu_current_frequency, gpu.currentFrequencyHz?.let { mhzText(it / HZ_PER_MHZ) })
    }
}

private const val HZ_PER_MHZ = 1_000_000L
