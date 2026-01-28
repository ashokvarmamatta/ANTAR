package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.domain.model.Network

interface NetworkRepository {
    fun getNetwork(): Network
}
