package com.ashes.dev.works.system.core.internals.antar.domain.model

/** One installed package. Icons are not part of the model; the UI loads them on demand. */
data class AppDetail(
    val appName: String,
    val packageName: String,
    val version: String?,
    val targetSdk: Int,
    val nativeLibraryDir: String?,
    val isSystemApp: Boolean
)

enum class AppFilter { ALL, SYSTEM, USER }
