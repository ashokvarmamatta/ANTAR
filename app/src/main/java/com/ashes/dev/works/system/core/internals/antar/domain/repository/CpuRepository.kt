package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu

interface CpuRepository {
    fun getCpu(): Cpu
}
