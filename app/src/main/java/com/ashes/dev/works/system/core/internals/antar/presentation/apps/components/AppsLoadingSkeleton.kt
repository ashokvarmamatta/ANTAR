package com.ashes.dev.works.system.core.internals.antar.presentation.apps.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.shimmer

@Composable
internal fun AppsLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmer()
            )
        }
    }
}
