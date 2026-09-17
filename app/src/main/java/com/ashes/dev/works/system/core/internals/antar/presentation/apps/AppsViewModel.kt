package com.ashes.dev.works.system.core.internals.antar.presentation.apps

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.ui.messageRes
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppFilter
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.GiveAppsConsentUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.LoadInstalledAppsUseCase
import com.ashes.dev.works.system.core.internals.antar.domain.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppsViewModel(
    private val loadInstalledApps: LoadInstalledAppsUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val giveAppsConsent: GiveAppsConsentUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private sealed interface Load {
        data object Idle : Load
        data object Loading : Load
        data class Loaded(val apps: List<AppDetail>) : Load
        data class Failed(val message: Int) : Load
    }

    private val load = MutableStateFlow<Load>(Load.Idle)
    private var loadJob: Job? = null

    // Filter, search and the expanded row survive rotation and process death.
    private val filter = savedStateHandle.getStateFlow(KEY_FILTER, AppFilter.ALL)
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val searchOpen = savedStateHandle.getStateFlow(KEY_SEARCH_OPEN, false)
    private val expanded = savedStateHandle.getStateFlow<String?>(KEY_EXPANDED, null)

    val uiState: StateFlow<AppsUiState> = combine(
        observeSettings(), load, filter, query, combine(searchOpen, expanded, ::Pair)
    ) { settings, load, filter, query, (searchOpen, expanded) ->
        when {
            !settings.appsConsentGiven -> AppsUiState.ConsentRequired
            load is Load.Loaded -> AppsUiState.Content(
                allApps = load.apps,
                visibleApps = load.apps.filter { app ->
                    val matchesFilter = when (filter) {
                        AppFilter.ALL -> true
                        AppFilter.SYSTEM -> app.isSystemApp
                        AppFilter.USER -> !app.isSystemApp
                    }
                    matchesFilter && (query.isBlank() ||
                        app.appName.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true))
                },
                filter = filter,
                query = query,
                isSearchOpen = searchOpen,
                expandedPackage = expanded
            )
            load is Load.Failed -> AppsUiState.Error(load.message)
            else -> AppsUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppsUiState.Loading)

    init {
        // After the user has consented once, never show the disclosure again: load straight away.
        viewModelScope.launch {
            if (observeSettings().first().appsConsentGiven) loadApps()
        }
    }

    /** Called from the one-time disclosure. Records consent, then loads. */
    fun giveConsent() {
        viewModelScope.launch {
            giveAppsConsent()
            loadApps()
        }
    }

    fun loadApps() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (load.value !is Load.Loaded) load.value = Load.Loading
            loadInstalledApps().collect { result ->
                load.value = when (result) {
                    is AppResult.Success -> Load.Loaded(result.data)
                    is AppResult.Failure -> Load.Failed(result.error.messageRes())
                }
            }
        }
    }

    fun setFilter(value: AppFilter) {
        savedStateHandle[KEY_FILTER] = value
    }

    fun setQuery(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun toggleSearch() {
        val open = !searchOpen.value
        savedStateHandle[KEY_SEARCH_OPEN] = open
        if (!open) savedStateHandle[KEY_QUERY] = ""
    }

    fun toggleExpanded(packageName: String) {
        savedStateHandle[KEY_EXPANDED] = if (expanded.value == packageName) null else packageName
    }

    private companion object {
        const val KEY_FILTER = "apps_filter"
        const val KEY_QUERY = "apps_query"
        const val KEY_SEARCH_OPEN = "apps_search_open"
        const val KEY_EXPANDED = "apps_expanded"
    }
}
