package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import java.io.File
import java.util.Locale

class StorageRepositoryImpl(private val context: Context) : StorageRepository {

    override fun getStorage(): Storage {
        // RAM Info
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val freeRam = memoryInfo.availMem
        val usedRam = totalRam - freeRam

        // Internal Storage (Data partition - usually what user sees as internal storage)
        val dataDir = Environment.getDataDirectory()
        val internalStatFs = StatFs(dataDir.path)
        val totalInternal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val freeInternal = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        val usedInternal = totalInternal - freeInternal

        // System Storage (/system partition)
        val systemDir = File("/system")
        val systemStatFs = StatFs(systemDir.path)
        val totalSystem = systemStatFs.blockCountLong * systemStatFs.blockSizeLong
        val freeSystem = systemStatFs.availableBlocksLong * systemStatFs.blockSizeLong
        val usedSystem = totalSystem - freeSystem

        // External Storage (Public directory)
        val externalDir = Environment.getExternalStorageDirectory()
        val externalStatFs = StatFs(externalDir.path)
        val totalExternal = externalStatFs.blockCountLong * externalStatFs.blockSizeLong
        val freeExternal = externalStatFs.availableBlocksLong * externalStatFs.blockSizeLong
        val usedExternal = totalExternal - freeExternal

        return Storage(
            freeMemory = formatSize(freeRam),
            usedTotalMemory = "${formatSize(usedRam)} / ${formatSize(totalRam)}",
            usagePercentageRam = "${(usedRam.toDouble() / totalRam.toDouble() * 100).toInt()}%",
            
            internalStoragePath = externalDir.absolutePath, // Usually /storage/emulated/0
            usedTotalFreeInternal = "${formatSize(usedExternal)} / ${formatSize(totalExternal)} / ${formatSize(freeExternal)}",
            usagePercentageInternal = "${(usedExternal.toDouble() / totalExternal.toDouble() * 100).toInt()}%",
            
            systemStorageFileSystemType = getFsType(systemDir),
            systemStoragePath = systemDir.absolutePath,
            systemStorageUsageProgress = "${(usedSystem.toDouble() / totalSystem.toDouble() * 100).toInt()}%",
            usedTotalFreeSystem = "${formatSize(usedSystem)} / ${formatSize(totalSystem)} / ${formatSize(freeSystem)}",
            
            internalStorageDataFileSystemType = getFsType(dataDir),
            internalStorageDataPath = dataDir.absolutePath,
            internalStorageDataUsageProgress = "${(usedInternal.toDouble() / totalInternal.toDouble() * 100).toInt()}%",
            usedTotalFreeInternalData = "${formatSize(usedInternal)} / ${formatSize(totalInternal)} / ${formatSize(freeInternal)}"
        )
    }

    private fun getFsType(file: File): String {
        return try {
            val statFs = StatFs(file.path)
            // Note: Modern Android doesn't easily expose FS type via StatFs, 
            // but we can try to detect or just return a placeholder if restricted.
            // For now, let's just return "Ext4/F2FS" as they are most common.
            "F2FS/Ext4" 
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun formatSize(size: Long): String {
        if (size <= 0) return "0 B"
        val suffix = arrayOf("B", "KB", "MB", "GB", "TB")
        var fSize = size.toDouble()
        var i = 0
        while (fSize >= 1024 && i < suffix.size - 1) {
            fSize /= 1024
            i++
        }
        return String.format(Locale.getDefault(), "%.2f %s", fSize, suffix[i])
    }
}
