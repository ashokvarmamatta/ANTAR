package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppError
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.Location
import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/** Live location fixes with GNSS status; a failing stream ends with one [AppResult.Failure] instead of throwing. */
class ObserveLocationUseCase(private val repository: LocationRepository) {
    operator fun invoke(): Flow<AppResult<Location>> =
        repository.observeLocation()
            .map<Location, AppResult<Location>> { AppResult.Success(it) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(
                    AppResult.Failure(
                        when (e) {
                            is SecurityException -> AppError.PermissionDenied
                            is UnsupportedOperationException -> AppError.Unavailable
                            else -> AppError.Unexpected(e)
                        }
                    )
                )
            }
}
