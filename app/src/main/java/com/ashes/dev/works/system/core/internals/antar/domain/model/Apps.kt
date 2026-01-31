package com.ashes.dev.works.system.core.internals.antar.domain.model

import android.graphics.drawable.Drawable

data class Apps(
    val appCount: String,
    val appList: List<AppDetail>
)

data class AppDetail(
    val appName: String,
    val packageName: String,
    val version: String,
    val apiLevelTag: String,
    val architectureTag: String,
    val isSystemApp: Boolean,
    val icon: Drawable? = null
)