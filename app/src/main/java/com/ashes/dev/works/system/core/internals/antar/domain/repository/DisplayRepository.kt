package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.DisplayInfo

interface DisplayRepository {
    suspend fun getDisplayInfo(): AppResult<DisplayInfo>
}
