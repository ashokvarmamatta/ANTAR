package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.Apps
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository

class AppsRepositoryImpl(private val context: Context) : AppsRepository {
    override fun getApps(): Apps {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(0)
        
        val appList = packages.mapNotNull { packageInfo ->
            val appInfo = packageInfo.applicationInfo ?: return@mapNotNull null
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val packageName = packageInfo.packageName
            val version = packageInfo.versionName ?: "Unknown"
            val apiLevel = appInfo.targetSdkVersion.toString()
            
            val architecture = appInfo.nativeLibraryDir ?: "Unknown"
            
            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                             (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            AppDetail(
                appName = appName,
                packageName = packageName,
                version = version,
                apiLevelTag = apiLevel,
                architectureTag = architecture,
                isSystemApp = isSystemApp,
                icon = null // Do not load icons here to avoid memory pressure and lag
            )
        }.sortedBy { it.appName.lowercase() }

        return Apps(
            appCount = "${appList.size} apps installed",
            appList = appList
        )
    }
}
