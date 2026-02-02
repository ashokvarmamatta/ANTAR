package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.AppsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(viewModel: AppsViewModel = koinViewModel()) {
    val appsState by viewModel.appsState.collectAsState()
    val tabs = listOf("All", "System", "User")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var expandedAppPackageName by remember { mutableStateOf<String?>(null) }

    if (appsState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val apps = appsState!!

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Selection Row with Search Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.weight(1f)
            ) {
                tabs.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                        onClick = { selectedTabIndex = index },
                        selected = selectedTabIndex == index
                    ) {
                        Text(label)
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                Icon(
                    imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Toggle Search"
                )
            }
        }

        // Search Bar (Shown below categories when expanded)
        AnimatedVisibility(
            visible = isSearchExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                placeholder = { Text("Search apps...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
        }

        val filteredApps = remember(selectedTabIndex, apps.appList, searchQuery) {
            apps.appList.filter { app ->
                val matchesCategory = when (selectedTabIndex) {
                    1 -> app.isSystemApp
                    2 -> !app.isSystemApp
                    else -> true
                }
                val matchesSearch = app.appName.contains(searchQuery, ignoreCase = true) ||
                        app.packageName.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }

        AppList(
            appList = filteredApps,
            expandedAppPackageName = expandedAppPackageName,
            onExpandApp = { expandedAppPackageName = if (expandedAppPackageName == it) null else it }
        )
    }
}

@Composable
fun AppList(
    appList: List<AppDetail>,
    expandedAppPackageName: String?,
    onExpandApp: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "${appList.size} Apps",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(appList, key = { it.packageName }) { app ->
            AppItem(
                app = app,
                isExpanded = expandedAppPackageName == app.packageName,
                onClick = { onExpandApp(app.packageName) }
            )
        }
    }
}

@Composable
fun AppItem(
    app: AppDetail,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var iconDrawable by remember(app.packageName) { mutableStateOf<Drawable?>(null) }
    
    LaunchedEffect(app.packageName) {
        withContext(Dispatchers.IO) {
            try {
                iconDrawable = context.packageManager.getApplicationIcon(app.packageName)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val bwMatrix = remember { ColorMatrix().apply { setToSaturation(0f) } }
                
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    iconDrawable?.let { drawable ->
                        Image(
                            bitmap = drawable.toBitmap().asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = ColorFilter.colorMatrix(bwMatrix),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabelValue("Ver", app.version)
                        LabelValue("API", app.apiLevelTag)
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
                    TextButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", app.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("App Info")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (intent != null) {
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Open App")
                    }
                }
            }
        }
    }
}

@Composable
fun LabelValue(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
