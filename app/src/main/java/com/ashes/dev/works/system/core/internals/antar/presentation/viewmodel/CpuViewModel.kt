package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.domain.model.Cpu
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CpuViewModel(private val cpuRepository: CpuRepository) : ViewModel() {
    private val _cpu = MutableStateFlow<Cpu?>(null)
    val cpu: StateFlow<Cpu?> = _cpu

    init {
        viewModelScope.launch {
            _cpu.value = cpuRepository.getCpu()
        }
    }
}
