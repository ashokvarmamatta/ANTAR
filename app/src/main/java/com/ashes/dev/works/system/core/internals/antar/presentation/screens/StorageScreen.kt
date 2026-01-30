package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.StorageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StorageScreen(viewModel: StorageViewModel = koinViewModel()) {
    val storage = viewModel.getStorage()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item(key = "ram") {
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
            InfoRow("Usage Progress Bar", storage.systemStorageUsageProgress)
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
            InfoRow("Usage Progress Bar", storage.internalStorageDataUsageProgress)
            InfoRow("Used / Total / Free", storage.usedTotalFreeInternalData, singleLine = false)
        }
    }
}
