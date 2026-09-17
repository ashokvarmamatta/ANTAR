package com.ashes.dev.works.system.core.internals.antar.presentation.cpu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.CpuCore
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GetCpuInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CpuViewModel(
    private val getCpuInfo: GetCpuInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CpuUiState>(CpuUiState.Loading)
    val uiState: StateFlow<CpuUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = CpuUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = getCpuInfo()) {
                is AppResult.Success -> CpuUiState.Content(result.data, groupCores(result.data.cores))
                is AppResult.Failure -> CpuUiState.Error(result.error.messageRes())
            }
        }
    }

    private fun groupCores(cores: List<CpuCore>): List<CpuCoreGroup> =
        cores
            .groupBy { core -> core.valueOf(KEY_PART) to core.valueOf(KEY_REVISION) }
            .map { (partAndRevision, members) ->
                CpuCoreGroup(
                    key = "core|${partAndRevision.first}|${partAndRevision.second}",
                    processors = members.map { it.processor },
                    fields = members.first().fields.filter { it.key !in HIDDEN_KEYS }
                )
            }

    private fun CpuCore.valueOf(key: String): String? = fields.firstOrNull { it.key == key }?.value

    private companion object {
        const val KEY_PART = "CPU part"
        const val KEY_REVISION = "CPU revision"

        /** Shown elsewhere (Features), meaningless per core (BogoMIPS) or already in the title. */
        val HIDDEN_KEYS = setOf("Features", "BogoMIPS", "processor")
    }
}
