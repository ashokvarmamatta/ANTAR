package com.ashes.dev.works.system.core.internals.antar.presentation.apps

import androidx.annotation.StringRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppFilter

sealed interface AppsUiState {
    data object Loading : AppsUiState

    /** The one-time installed-apps disclosure has not been accepted yet. */
    data object ConsentRequired : AppsUiState

    data class Content(
        val allApps: List<AppDetail>,
        val visibleApps: List<AppDetail>,
        val filter: AppFilter,
        val query: String,
        val isSearchOpen: Boolean,
        val expandedPackage: String?
    ) : AppsUiState

    data class Error(@param:StringRes val message: Int) : AppsUiState
}
