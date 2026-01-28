package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.Apps
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository

class AppsRepositoryImpl(private val context: Context) : AppsRepository {
    override fun getApps(): Apps {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val count = packages.size

        return Apps(
            appCount = "$count apps installed",
            appName = "- - -",
            packageName = "- - -",
            version = "- - -",
            apiLevelTag = "- - -",
            architectureTag = "- - -"
        )
    }
}
