package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.CpuRepository

class CpuViewModel(private val cpuRepository: CpuRepository) : ViewModel() {
    fun getCpu() = cpuRepository.getCpu()
}
