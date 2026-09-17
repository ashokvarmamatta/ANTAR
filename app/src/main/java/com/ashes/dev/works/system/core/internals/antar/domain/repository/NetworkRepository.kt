package com.ashes.dev.works.system.core.internals.antar.domain.repository

import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    /** The default network, updated live as it changes. Needs no runtime permission. */
    fun observeActiveConnection(): Flow<ActiveConnection>

    /** Wi-Fi radio and link, updated live. Identity fields fill in once location access is granted. */
    fun observeWifiState(): Flow<WifiState>

    suspend fun getTelephonyInfo(): AppResult<TelephonyInfo>
}
