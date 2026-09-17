package com.ashes.dev.works.system.core.internals.antar.data.local.cache

import android.content.Context
import androidx.core.content.edit
import com.ashes.dev.works.system.core.internals.antar.data.local.preferences.LEGACY_APPS_CACHE_KEY
import com.ashes.dev.works.system.core.internals.antar.data.local.preferences.LEGACY_PREFS_NAME
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Metadata-only copy of the last installed-apps scan, so the Apps tab opens instantly. It lives in
 * `noBackupFilesDir`: excluded from Auto Backup and device transfer, and deleted on uninstall.
 * Releases before 1.6 kept it inside the settings SharedPreferences; that copy is moved here once.
 */
class AppsCacheDataSource(
    private val context: Context,
    private val io: CoroutineDispatcher
) {
    private val file = File(context.noBackupFilesDir, "apps_cache.txt")
    private val mutex = Mutex()

    suspend fun read(): List<AppDetail> = withContext(io) {
        mutex.withLock {
            migrateLegacyCache()
            if (!file.exists()) emptyList() else decode(file.readText())
        }
    }

    suspend fun write(apps: List<AppDetail>) = withContext(io) {
        mutex.withLock { file.writeText(encode(apps)) }
    }

    private fun migrateLegacyCache() {
        val legacy = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
        val raw = legacy.getString(LEGACY_APPS_CACHE_KEY, null) ?: return
        if (!file.exists() && raw.isNotEmpty()) file.writeText(raw)
        legacy.edit { remove(LEGACY_APPS_CACHE_KEY) }
    }

    private fun encode(list: List<AppDetail>): String =
        list.joinToString(REC) { a ->
            listOf(a.appName, a.packageName, a.version.orEmpty(), a.targetSdk.toString(), a.nativeLibraryDir.orEmpty(), a.isSystemApp.toString())
                .joinToString(FIELD)
        }

    private fun decode(raw: String): List<AppDetail> {
        if (raw.isEmpty()) return emptyList()
        return raw.split(REC).mapNotNull { row ->
            val p = row.split(FIELD)
            if (p.size < 6) return@mapNotNull null
            AppDetail(
                appName = p[0],
                packageName = p[1],
                version = p[2].ifEmpty { null },
                targetSdk = p[3].toIntOrNull() ?: 0,
                nativeLibraryDir = p[4].ifEmpty { null },
                isSystemApp = p[5].toBoolean()
            )
        }
    }

    private companion object {
        // Printable separators unlikely to occur in app names / package ids.
        const val FIELD = "|@F@|"
        const val REC = "|@R@|"
    }
}
