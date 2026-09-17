package com.ashes.dev.works.system.core.internals.antar.presentation.apps

import android.content.Context
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Decodes launcher icons at the size they are drawn (not the drawable's intrinsic 192–432px) and
 * keeps them in a bounded memory cache, so flinging through hundreds of apps does not re-decode
 * every row. Memory stays under ~8MB whatever the number of apps.
 */
class AppIconLoader(
    private val context: Context,
    private val io: CoroutineDispatcher
) {
    private val cache = object : LruCache<String, ImageBitmap>(MAX_CACHE_BYTES) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = value.width * value.height * 4
    }

    fun cached(packageName: String, sizePx: Int): ImageBitmap? = cache.get(key(packageName, sizePx))

    suspend fun load(packageName: String, sizePx: Int): ImageBitmap? {
        cached(packageName, sizePx)?.let { return it }
        return withContext(io) {
            runCatching {
                context.packageManager.getApplicationIcon(packageName)
                    .toBitmap(width = sizePx, height = sizePx)
                    .asImageBitmap()
            }.getOrNull()?.also { cache.put(key(packageName, sizePx), it) }
        }
    }

    private fun key(packageName: String, sizePx: Int) = "$packageName@$sizePx"

    private companion object {
        const val MAX_CACHE_BYTES = 8 * 1024 * 1024
    }
}
