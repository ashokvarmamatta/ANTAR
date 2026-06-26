package com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashes.dev.works.system.core.internals.antar.data.preference.ThemePreferences
import com.ashes.dev.works.system.core.internals.antar.domain.model.AppDetail
import com.ashes.dev.works.system.core.internals.antar.domain.model.Apps
import com.ashes.dev.works.system.core.internals.antar.domain.repository.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppsViewModel(
    private val appsRepository: AppsRepository,
    private val prefs: ThemePreferences
) : ViewModel() {
    private val _appsState = MutableStateFlow<Apps?>(null)
    val appsState = _appsState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // After the user has consented once, never show the disclosure again — load the cached
        // list instantly (if any) and re-scan in the background to pick up new/removed apps.
        if (prefs.appsConsentGiven) loadApps()
    }

    /** Called from the one-time disclosure. Records consent, then loads. */
    fun giveConsent() {
        prefs.appsConsentGiven = true
        loadApps()
    }

    fun loadApps() {
        if (_appsState.value != null || _isLoading.value) return
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Show the cached list immediately (no spinner) for an instant open.
            val cached = decodeApps(prefs.cachedAppsRaw)
            if (cached.isNotEmpty()) {
                _appsState.value = Apps("${cached.size} apps installed", cached)
            }
            // 2) Re-scan and sync. Spinner only if we had nothing cached to show.
            if (_appsState.value == null) _isLoading.value = true
            try {
                val fresh = appsRepository.getApps()
                _appsState.value = fresh
                prefs.cachedAppsRaw = encodeApps(fresh.appList)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Lightweight cache (metadata only; icons are loaded per-row in the UI) ──
    private fun encodeApps(list: List<AppDetail>): String =
        list.joinToString(REC) { a ->
            listOf(a.appName, a.packageName, a.version, a.apiLevelTag, a.architectureTag, a.isSystemApp.toString())
                .joinToString(FIELD)
        }

    private fun decodeApps(raw: String): List<AppDetail> {
        if (raw.isEmpty()) return emptyList()
        return raw.split(REC).mapNotNull { row ->
            val p = row.split(FIELD)
            if (p.size < 6) return@mapNotNull null
            AppDetail(
                appName = p[0],
                packageName = p[1],
                version = p[2],
                apiLevelTag = p[3],
                architectureTag = p[4],
                isSystemApp = p[5].toBoolean(),
                icon = null
            )
        }
    }

    private companion object {
        // Printable separators unlikely to occur in app names / package ids.
        const val FIELD = "|@F@|"
        const val REC = "|@R@|"
    }
}
