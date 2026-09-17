package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.domain.repository.BatteryRepository

/** Stores one battery reading now. Returns false when there was no battery state to read. */
class LogBatteryReadingUseCase(private val repository: BatteryRepository) {
    suspend operator fun invoke(): Boolean = repository.logCurrentBattery()
}
