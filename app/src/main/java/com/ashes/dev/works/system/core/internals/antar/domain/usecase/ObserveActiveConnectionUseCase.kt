package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppError
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/** Live default-network details; a failing stream ends with one [AppResult.Failure] instead of throwing. */
class ObserveActiveConnectionUseCase(private val repository: NetworkRepository) {
    operator fun invoke(): Flow<AppResult<ActiveConnection>> =
        repository.observeActiveConnection()
            .map<ActiveConnection, AppResult<ActiveConnection>> { AppResult.Success(it) }
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
