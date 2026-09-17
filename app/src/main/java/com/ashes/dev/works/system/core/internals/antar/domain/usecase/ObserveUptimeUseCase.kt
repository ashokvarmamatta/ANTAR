package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow

class ObserveUptimeUseCase(private val repository: SystemRepository) {
    operator fun invoke(): Flow<Long> = repository.observeUptimeMillis()
}
