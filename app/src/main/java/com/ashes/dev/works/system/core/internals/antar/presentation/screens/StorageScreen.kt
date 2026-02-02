package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.StorageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StorageScreen(viewModel: StorageViewModel = koinViewModel()) {
    val storageState by viewModel.storageState.collectAsState()

    if (storageState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val storage = storageState!!

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item(key = "header") {
            StorageHeader(storage = storage)
        }

        item(key = "ram") {
            Spacer(modifier = Modifier.height(16.dp))
            RamCard(storage = storage)
        }

        item(key = "internal_storage") { 
            Spacer(modifier = Modifier.height(16.dp))
            InternalStorageCard(storage = storage)
        }

        item(key = "system_storage") { 
            Spacer(modifier = Modifier.height(16.dp))
            SystemStorageCard(storage = storage)
        }

        item(key = "internal_storage_data") { 
            Spacer(modifier = Modifier.height(16.dp))
            InternalStorageDataCard(storage = storage)
        }
    }
}

@Composable
private fun StorageHeader(storage: Storage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9)
        )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_storage),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Unspecified
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Memory & Storage",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "RAM Usage: ${storage.usagePercentageRam}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "ROM Usage: ${storage.usagePercentageInternal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RamCard(storage: Storage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RAM",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("Free Memory", storage.freeMemory)
            InfoRow("Used / Total Memory", storage.usedTotalMemory)
            InfoRow("Usage Percentage", storage.usagePercentageRam)
        }
    }
}

@Composable
private fun InternalStorageCard(storage: Storage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Internal Storage",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("Path", storage.internalStoragePath, singleLine = false)
            InfoRow("Used / Total / Free", storage.usedTotalFreeInternal, singleLine = false)
            InfoRow("Usage Percentage", storage.usagePercentageInternal)
        }
    }
}

@Composable
private fun SystemStorageCard(storage: Storage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "System storage",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("File System Type", storage.systemStorageFileSystemType)
            InfoRow("Path", storage.systemStoragePath, singleLine = false)
            InfoRow("Usage", storage.systemStorageUsageProgress)
            InfoRow("Used / Total / Free", storage.usedTotalFreeSystem, singleLine = false)
        }
    }
}

@Composable
private fun InternalStorageDataCard(storage: Storage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Internal Storage (Data)",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            InfoRow("File System Type", storage.internalStorageDataFileSystemType)
            InfoRow("Path", storage.internalStorageDataPath, singleLine = false)
            InfoRow("Usage", storage.internalStorageDataUsageProgress)
            InfoRow("Used / Total / Free", storage.usedTotalFreeInternalData, singleLine = false)
        }
    }
}
