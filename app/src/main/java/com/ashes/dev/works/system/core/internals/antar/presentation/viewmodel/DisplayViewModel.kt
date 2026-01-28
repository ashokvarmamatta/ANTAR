package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ashes.dev.works.system.core.internals.antar.domain.repository.DisplayRepository

class DisplayViewModel(private val displayRepository: DisplayRepository) : ViewModel() {
    fun getDisplay() = displayRepository.getDisplay()
}
