package com.ashes.dev.works.system.core.internals.antar.presentation.cpu

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuInfoField

sealed interface CpuUiState {
    data object Loading : CpuUiState
    data class Content(val cpu: CpuInfo, val coreGroups: List<CpuCoreGroup>) : CpuUiState
    data class Error(@param:StringRes val message: Int) : CpuUiState
}

/** Cores that report the same part and revision, shown as one card. Built once in the ViewModel. */
data class CpuCoreGroup(
    /** Stable list key. */
    val key: String,
    val processors: List<Int>,
    /** /proc/cpuinfo lines of the first core in the group, minus the per-core noise. */
    val fields: List<CpuInfoField>
)
