package com.ashes.dev.works.system.core.internals.antar.domain.usecase

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.DisplayInfo
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository

class GetDisplayInfoUseCase(private val repository: DisplayRepository) {
    suspend operator fun invoke(): AppResult<DisplayInfo> = repository.getDisplayInfo()
}
