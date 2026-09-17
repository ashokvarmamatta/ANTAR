package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.domain.model.BatteryRecord
import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository
import kotlinx.coroutines.flow.Flow

/** Stored battery readings at or after [sinceMillis], oldest first. */
class ObserveBatteryHistoryUseCase(private val repository: BatteryRepository) {
    operator fun invoke(sinceMillis: Long): Flow<List<BatteryRecord>> =
        repository.getBatteryHistory(sinceMillis)
}
