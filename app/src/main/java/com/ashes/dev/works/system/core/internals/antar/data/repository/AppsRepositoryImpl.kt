package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.data.local.cache.AppsCacheDataSource
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AppsRepositoryImpl(
    private val context: Context,
    private val cache: AppsCacheDataSource,
    private val io: CoroutineDispatcher
) : AppsRepository {

    override suspend fun getInstalledAppCount(): AppResult<Int> = withContext(io) {
        appResultOf { installedPackages().size }
    }

    override suspend fun getInstalledApps(): AppResult<List<AppDetail>> = withContext(io) {
        appResultOf {
            val packageManager = context.packageManager
            installedPackages().mapNotNull { packageInfo ->
                val appInfo = packageInfo.applicationInfo ?: return@mapNotNull null
                AppDetail(
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = packageInfo.packageName,
                    version = packageInfo.versionName,
                    targetSdk = appInfo.targetSdkVersion,
                    nativeLibraryDir = appInfo.nativeLibraryDir,
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                        (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                )
            }.sortedBy { it.appName.lowercase() }
        }
    }

    override suspend fun getCachedApps(): List<AppDetail> = cache.read()

    override suspend fun cacheApps(apps: List<AppDetail>) = cache.write(apps)

    private fun installedPackages(): List<PackageInfo> {
        val packageManager = context.packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
    }
}
