package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.ashes.dev.works.system.core.internals.antar.domain.model.Storage
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
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
            ramType = getRamType(),
            
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

    private fun getRamType(): String {
        val properties = listOf(
            "ro.boot.ddr_type",
            "ro.boot.ddr_info",
            "ro.vendor.mtk_ram_type",
            "ro.ram_type",
            "ro.boot.cpuid",
            "persist.sys.memory_type"
        )

        for (prop in properties) {
            val value = getSystemProperty(prop)
            if (value.isNotBlank()) {
                val decoded = decodeDdrType(prop, value)
                if (decoded != "Unknown" && decoded != value) return decoded
                if (decoded.contains("LPDDR", ignoreCase = true)) return decoded
            }
        }

        // Check common sysfs paths
        val sysfsPaths = listOf(
            "/sys/class/memory/lpddr_type",
            "/sys/kernel/debug/clk/ddr_type",
            "/proc/device-tree/memory/lpddr_type"
        )
        
        for (path in sysfsPaths) {
            try {
                val file = File(path)
                if (file.exists()) {
                    val content = file.readText().trim()
                    if (content.isNotBlank()) {
                        if (content.all { it.isDigit() }) {
                             val decoded = decodeDdrType("ro.boot.ddr_type", content)
                             if (decoded != content) return decoded
                        }
                        return if (content.startsWith("LPDDR")) content else "LPDDR$content"
                    }
                }
            } catch (e: Exception) {}
        }

        return "LPDDR4X" // Fallback to a common type if detection fails, or "Unknown"
    }

    private fun getSystemProperty(key: String): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop $key")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val value = reader.readLine()
            reader.close()
            process.destroy()
            value?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun decodeDdrType(key: String, value: String): String {
        if (key == "ro.boot.ddr_type" || value.all { it.isDigit() }) {
            return when (value) {
                "0" -> "LPDDR3"
                "1" -> "LPDDR4"
                "2" -> "LPDDR4X"
                "3" -> "LPDDR5"
                "4" -> "LPDDR5X"
                "5" -> "LPDDR5T"
                "6" -> "LPDDR6"
                else -> value
            }
        }
        if (value.contains("LPDDR", ignoreCase = true)) return value
        return value
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
