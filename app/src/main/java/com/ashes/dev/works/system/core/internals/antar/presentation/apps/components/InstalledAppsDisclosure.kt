package com.ashes.dev.works.system.core.internals.antar.presentation.apps.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale

@Composable
internal fun InstalledAppsDisclosure(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Apps,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = AntarCyan
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.apps_disclosure_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.apps_disclosure_what),
            style = MaterialTheme.typography.bodySmall,
            color = AntarGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.apps_disclosure_storage),
            style = MaterialTheme.typography.bodySmall,
            color = AntarGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        val interactionSource = remember { MutableInteractionSource() }
        Button(
            onClick = onContinue,
            interactionSource = interactionSource,
            modifier = Modifier.pressScale(interactionSource),
            colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
        ) {
            Text(stringResource(R.string.apps_disclosure_continue), fontWeight = FontWeight.Bold, color = AntarDark)
        }
    }
}
