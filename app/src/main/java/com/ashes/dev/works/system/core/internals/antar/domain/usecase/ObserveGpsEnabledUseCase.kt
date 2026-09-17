package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/** Whether the GPS provider is switched on, updated when the user toggles it. */
class ObserveGpsEnabledUseCase(private val repository: LocationRepository) {
    operator fun invoke(): Flow<Boolean> = repository.observeGpsEnabled()
}
