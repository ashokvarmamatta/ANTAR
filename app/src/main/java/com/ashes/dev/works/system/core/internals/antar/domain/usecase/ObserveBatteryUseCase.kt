package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppError
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.Battery
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/** Live battery readings; a failing stream ends with one [AppResult.Failure] instead of throwing. */
class ObserveBatteryUseCase(private val repository: BatteryRepository) {
    operator fun invoke(): Flow<AppResult<Battery>> =
        repository.getBatteryInfo()
            .map<Battery, AppResult<Battery>> { AppResult.Success(it) }
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
