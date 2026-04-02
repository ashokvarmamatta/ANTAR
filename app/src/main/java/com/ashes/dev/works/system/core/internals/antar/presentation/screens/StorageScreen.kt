package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.*
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.StorageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StorageScreen(viewModel: StorageViewModel = koinViewModel()) {
    val storageState by viewModel.storageState.collectAsState()

    if (storageState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AntarCyan)
        }
        return
    }

    val storage = storageState!!

    // Parse usage percentage from string like "45%"
    val ramPct = storage.usagePercentageRam.replace("%", "").trim().toFloatOrNull() ?: 0f
    val internalPct = storage.usagePercentageInternal.replace("%", "").trim().toFloatOrNull() ?: 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GradientHeaderCard {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Storage,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarPurple
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Memory & Storage",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RAM: ${storage.usagePercentageRam} \u2022 ROM: ${storage.usagePercentageInternal}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AntarGray
                        )
                    }
                }
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "RAM", icon = Icons.Outlined.Memory)
                InfoRow("Type", storage.ramType)
                InfoRow("Free Memory", storage.freeMemory)
                InfoRow("Used / Total", storage.usedTotalMemory)

                Spacer(modifier = Modifier.height(8.dp))

                val animatedRam by animateFloatAsState(
                    targetValue = ramPct / 100f,
                    animationSpec = tween(1200, easing = FastOutSlowInEasing),
                    label = "ram"
                )
                GradientProgressBar(
                    progress = animatedRam,
                    height = 8.dp,
                    colors = listOf(AntarCyan, AntarBlue)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = storage.usagePercentageRam,
                    style = MaterialTheme.typography.labelSmall,
                    color = AntarCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Internal Storage", icon = Icons.Outlined.Folder, accentColor = AntarPurple)
                InfoRow("Path", storage.internalStoragePath, singleLine = false)
                InfoRow("Used / Total / Free", storage.usedTotalFreeInternal, singleLine = false)

                Spacer(modifier = Modifier.height(8.dp))

                val animatedInternal by animateFloatAsState(
                    targetValue = internalPct / 100f,
                    animationSpec = tween(1200, easing = FastOutSlowInEasing),
                    label = "internal"
                )
                GradientProgressBar(
                    progress = animatedInternal,
                    height = 8.dp,
                    colors = listOf(AntarPurple, AntarPink)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = storage.usagePercentageInternal,
                    style = MaterialTheme.typography.labelSmall,
                    color = AntarPurple,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "System Storage", icon = Icons.Outlined.Dns, accentColor = AntarBlue)
                InfoRow("File System Type", storage.systemStorageFileSystemType)
                InfoRow("Path", storage.systemStoragePath, singleLine = false)
                InfoRow("Usage", storage.systemStorageUsageProgress)
                InfoRow("Used / Total / Free", storage.usedTotalFreeSystem, singleLine = false)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Internal Storage (Data)", icon = Icons.Outlined.Storage, accentColor = AntarGreen)
                InfoRow("File System Type", storage.internalStorageDataFileSystemType)
                InfoRow("Path", storage.internalStorageDataPath, singleLine = false)
                InfoRow("Usage", storage.internalStorageDataUsageProgress)
                InfoRow("Used / Total / Free", storage.usedTotalFreeInternalData, singleLine = false)
            }
        }
    }
}
