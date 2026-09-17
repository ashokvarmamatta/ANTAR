package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository

class GetTelephonyInfoUseCase(private val repository: NetworkRepository) {
    suspend operator fun invoke(): AppResult<TelephonyInfo> = repository.getTelephonyInfo()
}
