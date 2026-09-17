package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.MemoryUsage
import com.ashes.dev.works.system.core.internals.antar.domain.model.RamType
import com.ashes.dev.works.system.core.internals.antar.domain.model.StorageInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.VolumeUsage
import com.ashes.dev.works.system.core.internals.antar.domain.repository.StorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

class StorageRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : StorageRepository {

    /**
     * RAM type is fixed hardware and resolving it forks several `getprop` processes, so it is read
     * once. SYNCHRONIZED (the default) makes concurrent first reads safe; it is only touched on [io].
     */
    private val ramType: RamType? by lazy { readRamType() }

    override suspend fun getStorageInfo(): AppResult<StorageInfo> = withContext(io) {
        appResultOf {
            val activityManager = context.getSystemService(ActivityManager::class.java)
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val mounts = readMounts()
            val sharedDir = sharedStorageDirectory()
            val dataDir = Environment.getDataDirectory()

            StorageInfo(
                ram = MemoryUsage(totalBytes = memoryInfo.totalMem, availableBytes = memoryInfo.availMem),
                ramType = ramType,
                internalStorage = volumeUsage(sharedDir, mounts),
                systemPartition = try {
                    volumeUsage(File(SYSTEM_PATH), mounts)
                } catch (e: IllegalArgumentException) {
                    // StatFs throws IllegalArgumentException when the path cannot be stat-ed.
                    null
                },
                dataPartition = volumeUsage(dataDir, mounts)
            )
        }
    }

    @Suppress("DEPRECATION") // The public shared-storage root; only its size is read, never its files.
    private fun sharedStorageDirectory(): File = Environment.getExternalStorageDirectory()

    private fun volumeUsage(dir: File, mounts: List<MountEntry>): VolumeUsage {
        val statFs = StatFs(dir.path)
        return VolumeUsage(
            path = dir.absolutePath,
            totalBytes = statFs.blockCountLong * statFs.blockSizeLong,
            freeBytes = statFs.availableBlocksLong * statFs.blockSizeLong,
            fileSystemType = fileSystemType(dir.absolutePath, mounts)
        )
    }

    /** Filesystem of the deepest mount point containing [path]; the last mount wins on overmounts. */
    private fun fileSystemType(path: String, mounts: List<MountEntry>): String? {
        val containing = mounts.filter { entry ->
            val prefix = entry.mountPoint.trimEnd('/') + "/"
            path == entry.mountPoint || path.startsWith(prefix)
        }
        val deepest = containing.maxOfOrNull { it.mountPoint.length } ?: return null
        return containing.lastOrNull { it.mountPoint.length == deepest }?.fileSystemType
    }

    /** Mount table of this process's namespace: `device mountPoint fsType options dump pass`. */
    private fun readMounts(): List<MountEntry> {
        val text = try {
            File(MOUNTS_PATH).readText()
        } catch (e: Exception) {
            return emptyList()
        }
        return text.lineSequence().mapNotNull { line ->
            val parts = line.trim().split(WHITESPACE)
            if (parts.size < 3) {
                null
            } else {
                // The kernel escapes spaces in mount points as \040.
                MountEntry(mountPoint = parts[1].replace("\\040", " "), fileSystemType = parts[2])
            }
        }.toList()
    }

    /**
     * Only an explicit module name reported by the device counts (e.g. "LPDDR4X"). Numeric codes are
     * vendor specific and are not decoded, because a guessed type would be invented hardware.
     */
    private fun readRamType(): RamType? {
        val fromProperties = RAM_TYPE_PROPERTIES.firstNotNullOfOrNull { key ->
            readSystemProperty(key)?.let { parseRamType(it) }
        }
        if (fromProperties != null) return fromProperties

        return RAM_TYPE_FILES.firstNotNullOfOrNull { path ->
            val content = try {
                File(path).readText()
            } catch (e: Exception) {
                null
            }
            content?.let { parseRamType(it) }
        }
    }

    private fun parseRamType(raw: String): RamType? {
        val match = RAM_TYPE_TOKEN.find(raw) ?: return null
        val token = match.value.uppercase().replace(WHITESPACE, "").replace("-", "")
        return RamType.entries.firstOrNull { it.token == token }
    }

    private fun readSystemProperty(key: String): String? = try {
        val process = ProcessBuilder("getprop", key).start()
        try {
            process.inputStream.bufferedReader().use { it.readLine() }?.trim()?.takeIf { it.isNotEmpty() }
        } finally {
            process.destroy()
        }
    } catch (e: Exception) {
        null
    }

    private data class MountEntry(val mountPoint: String, val fileSystemType: String)

    private companion object {
        const val SYSTEM_PATH = "/system"
        const val MOUNTS_PATH = "/proc/self/mounts"

        val WHITESPACE = Regex("\\s+")

        /** LPDDR4X, lpddr5, LP-DDR5X, DDR4 ... The optional suffix is X or T. */
        val RAM_TYPE_TOKEN = Regex("(LP[- ]?)?DDR\\s?\\d[XT]?(?![A-Z])", RegexOption.IGNORE_CASE)

        val RAM_TYPE_PROPERTIES = listOf(
            "ro.boot.ddr_type",
            "ro.boot.ddr_info",
            "ro.vendor.mtk_ram_type",
            "ro.ram_type",
            "persist.sys.memory_type"
        )

        val RAM_TYPE_FILES = listOf(
            "/sys/class/memory/lpddr_type",
            "/sys/kernel/debug/clk/ddr_type",
            "/proc/device-tree/memory/lpddr_type"
        )
    }
}
