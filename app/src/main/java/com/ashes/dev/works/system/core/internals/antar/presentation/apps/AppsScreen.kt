package com.ashes.dev.works.system.core.internals.antar.presentation.apps

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDimGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppFilter
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.components.AppItem
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.components.AppsLoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.apps.components.InstalledAppsDisclosure
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppsScreen(viewModel: AppsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "appsState"
    ) { state ->
        when (state) {
            AppsUiState.Loading -> AppsLoadingSkeleton()
            AppsUiState.ConsentRequired -> InstalledAppsDisclosure(onContinue = viewModel::giveConsent)
            is AppsUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::loadApps)
            is AppsUiState.Content -> AppsContent(
                state = state,
                onFilter = viewModel::setFilter,
                onQuery = viewModel::setQuery,
                onToggleSearch = viewModel::toggleSearch,
                onToggleExpanded = viewModel::toggleExpanded
            )
        }
    }
}

@Composable
private fun AppsContent(
    state: AppsUiState.Content,
    onFilter: (AppFilter) -> Unit,
    onQuery: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onToggleExpanded: (String) -> Unit
) {
    val filters = listOf(
        AppFilter.ALL to R.string.apps_filter_all,
        AppFilter.SYSTEM to R.string.apps_filter_system,
        AppFilter.USER to R.string.apps_filter_user
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                filters.forEachIndexed { index, (filter, label) ->
                    val interactionSource = remember { MutableInteractionSource() }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = filters.size),
                        onClick = { onFilter(filter) },
                        selected = state.filter == filter,
                        interactionSource = interactionSource,
                        modifier = Modifier.pressScale(interactionSource),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = AntarCyan.copy(alpha = 0.15f),
                            activeContentColor = AntarCyan
                        )
                    ) {
                        Text(stringResource(label), maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            val searchSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = onToggleSearch,
                interactionSource = searchSource,
                modifier = Modifier.pressScale(searchSource)
            ) {
                Icon(
                    imageVector = if (state.isSearchOpen) Icons.Outlined.Close else Icons.Outlined.Search,
                    contentDescription = stringResource(if (state.isSearchOpen) R.string.apps_search_close else R.string.apps_search_open),
                    tint = AntarCyan
                )
            }
        }

        AnimatedVisibility(
            visible = state.isSearchOpen,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                placeholder = { Text(stringResource(R.string.apps_search_hint), color = AntarGray) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = AntarCyan) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntarCyan,
                    unfocusedBorderColor = AntarDimGray.copy(alpha = 0.3f)
                )
            )
        }

        AdaptiveCardGrid {
            item(key = "count", span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = pluralStringResource(R.plurals.apps_count, state.visibleApps.size, state.visibleApps.size),
                    style = MaterialTheme.typography.titleSmall,
                    color = AntarGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (state.visibleApps.isEmpty()) {
                item(key = "empty", span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = stringResource(R.string.apps_empty_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AntarGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .animateItem()
                    )
                }
            }
            itemsIndexed(state.visibleApps, key = { _, app -> app.packageName }) { index, app ->
                AppItem(
                    app = app,
                    isExpanded = state.expandedPackage == app.packageName,
                    onClick = { onToggleExpanded(app.packageName) },
                    modifier = Modifier
                        .animateItem()
                        .staggeredEntry(index)
                )
            }
        }
    }
}
