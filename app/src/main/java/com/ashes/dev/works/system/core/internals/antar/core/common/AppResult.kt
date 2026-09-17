package com.ashes.dev.works.system.core.internals.antar.core.common

import kotlin.coroutines.cancellation.CancellationException

/**
 * The only way a repository reports success or failure. Exceptions never cross from the data layer
 * into a ViewModel: they are mapped to an [AppError] here, once.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

/** Why something could not be read. The UI turns each case into its own message. */
sealed interface AppError {
    /** A runtime permission the platform call needs is not granted. */
    data object PermissionDenied : AppError

    /** The device or Android version does not expose this information. */
    data object Unavailable : AppError

    /** Anything else; kept for logging, never shown to the user as raw text. */
    data class Unexpected(val cause: Throwable) : AppError
}

/**
 * Runs [block] and maps its outcome. [CancellationException] is always rethrown so structured
 * concurrency keeps working.
 */
inline fun <T> appResultOf(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: SecurityException) {
    AppResult.Failure(AppError.PermissionDenied)
} catch (e: UnsupportedOperationException) {
    AppResult.Failure(AppError.Unavailable)
} catch (e: Exception) {
    AppResult.Failure(AppError.Unexpected(e))
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data
