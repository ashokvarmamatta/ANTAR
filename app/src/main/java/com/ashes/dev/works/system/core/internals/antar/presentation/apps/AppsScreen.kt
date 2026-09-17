package com.ashes.dev.works.system.core.internals.antar.presentation.apps

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LabelValue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDimGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.shimmer
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.spatialSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.ui.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppFilter
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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

@Composable
private fun AppsLoadingSkeleton() {
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

@Composable
private fun InstalledAppsDisclosure(onContinue: () -> Unit) {
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

@Composable
private fun AppItem(
    app: AppDetail,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconLoader: AppIconLoader = koinInject()
) {
    val context = LocalContext.current
    val iconSizePx = with(LocalDensity.current) { 44.dp.roundToPx() }
    var iconBitmap by remember(app.packageName) {
        mutableStateOf<ImageBitmap?>(iconLoader.cached(app.packageName, iconSizePx))
    }
    LaunchedEffect(app.packageName, iconSizePx) {
        if (iconBitmap == null) iconBitmap = iconLoader.load(app.packageName, iconSizePx)
    }
    val cannotOpen = stringResource(R.string.apps_cannot_open)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape)
            .animateContentSize(animationSpec = LocalAnimationIntensity.current.spatialSpec())
            .bounceClick(pressedScale = 0.98f) { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    iconBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = AntarGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabelValue(R.string.apps_label_version, app.version ?: NO_VALUE, Modifier.weight(1f, fill = false))
                        LabelValue(R.string.apps_label_api, app.targetSdk.toString(), Modifier.weight(1f, fill = false))
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val infoSource = remember { MutableInteractionSource() }
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", app.packageName, null))
                            )
                        },
                        interactionSource = infoSource,
                        modifier = Modifier.pressScale(infoSource)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.apps_action_app_info))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    val openSource = remember { MutableInteractionSource() }
                    Button(
                        onClick = {
                            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (intent != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, cannotOpen, Toast.LENGTH_SHORT).show()
                            }
                        },
                        interactionSource = openSource,
                        modifier = Modifier.pressScale(openSource),
                        colors = ButtonDefaults.buttonColors(containerColor = AntarCyan, contentColor = AntarDark)
                    ) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.apps_action_open), fontWeight = FontWeight.Bold, color = AntarDark)
                    }
                }
            }
        }
    }
}
