package com.ashes.dev.works.system.core.internals.antar.domain.model

data class Apps(
    val appCount: String,
    val appName: String,
    val packageName: String,
    val version: String,
    val apiLevelTag: String,
    val architectureTag: String
)