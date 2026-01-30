package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import java.util.Locale

class StorageRepositoryImpl(private val context: Context) : StorageRepository {

    private lateinit var cachedStorage: Storage

    override fun getStorage(): Storage {
        if (::cachedStorage.isInitialized) {
            return cachedStorage
        }

        val internalStatFs = StatFs(Environment.getDataDirectory().path)
        val totalInternal = internalStatFs.blockCountLong * internalStatFs.blockSizeLong
        val freeInternal = internalStatFs.availableBlocksLong * internalStatFs.blockSizeLong
        val usedInternalVal = totalInternal - freeInternal

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val freeRam = memoryInfo.availMem
        val usedRam = totalRam - freeRam

        cachedStorage = Storage(
            freeMemory = formatSize(freeRam),
            usedTotalMemory = "${formatSize(usedRam)} / ${formatSize(totalRam)}",
            usagePercentageRam = "${(usedRam.toDouble() / totalRam.toDouble() * 100).toInt()}%",
            internalStoragePath = Environment.getDataDirectory().path,
            usedTotalFreeInternal = "${formatSize(usedInternalVal)} / ${formatSize(totalInternal)} / ${formatSize(freeInternal)}",
            usagePercentageInternal = "${(usedInternalVal.toDouble() / totalInternal.toDouble() * 100).toInt()}%",
            systemStorageFileSystemType = "- - -",
            systemStoragePath = "/system",
            systemStorageUsageProgress = "- - -",
            usedTotalFreeSystem = "- - -",
            internalStorageDataFileSystemType = "- - -",
            internalStorageDataPath = "/data",
            internalStorageDataUsageProgress = "- - -",
            usedTotalFreeInternalData = "- - -"
        )
        return cachedStorage
    }

    private fun formatSize(size: Long): String {
        val suffix = arrayOf("B", "KB", "MB", "GB", "TB")
        var fSize = size.toDouble()
        var i = 0
        while (fSize > 1024 && i < suffix.size - 1) {
            fSize /= 1024
            i++
        }
        return String.format(Locale.getDefault(), "%.2f %s", fSize, suffix[i])
    }
}
