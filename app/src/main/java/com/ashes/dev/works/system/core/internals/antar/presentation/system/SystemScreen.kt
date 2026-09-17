package com.ashes.dev.works.system.core.internals.antar.presentation.system

import android.text.format.Formatter
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.ui.ErrorState
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.CalendarDate
import com.ashes.dev.works.system.core.internals.antar.domain.model.SelinuxMode
import com.ashes.dev.works.system.core.internals.antar.domain.model.SystemInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WidevineInfo
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SystemScreen(viewModel: SystemViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "systemState"
    ) { state ->
        when (state) {
            SystemUiState.Loading -> LoadingSkeleton()
            is SystemUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is SystemUiState.Content -> SystemContent(state.info, viewModel.uptimeMillis)
        }
    }
}

@Composable
private fun SystemContent(info: SystemInfo, liveUptimeMillis: StateFlow<Long?>) {
    val locale = currentLocale()
    val versionName = versionNameRes(info.apiLevel)?.let { stringResource(it) }
    val releaseDate = info.releaseDate?.let { date ->
        val formatted = remember(date, locale) { formatDate(date, locale) }
        stringResource(R.string.system_header_released, formatted)
    }
    val yes = stringResource(R.string.common_yes)
    val no = stringResource(R.string.common_no)
    val enabled = stringResource(R.string.common_enabled)
    val disabled = stringResource(R.string.common_disabled)

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Android,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AntarGreen
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.system_header_version, info.androidVersion),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        versionName?.let { name ->
                            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = AntarCyan)
                        }
                        info.codename?.let { codename ->
                            Text(text = codename, style = MaterialTheme.typography.bodySmall, color = AntarGray)
                        }
                        releaseDate?.let { released ->
                            Text(text = released, style = MaterialTheme.typography.bodySmall, color = AntarGray)
                        }
                    }
                }
            }
        }

        item(key = "os") {
            PremiumCard(modifier = Modifier.staggeredEntry(1)) {
                SectionTitle(title = R.string.system_section_os, icon = Icons.Outlined.Settings)
                InfoRow(R.string.system_label_android_version, info.androidVersion)
                InfoRow(R.string.system_label_version_name, versionName)
                InfoRow(R.string.system_label_api_level, info.apiLevel.toString())
                InfoRow(R.string.system_label_build_number, info.buildNumber, singleLine = false)
                InfoRow(
                    R.string.system_label_build_time,
                    remember(info.buildTimeMillis, locale) { formatDateTime(info.buildTimeMillis, locale) }
                )
                InfoRow(R.string.system_label_build_id, info.buildId, singleLine = false)
                InfoRow(R.string.system_label_security_patch, info.securityPatch)
                InfoRow(R.string.system_label_baseband, info.baseband, singleLine = false)
                InfoRow(
                    R.string.system_label_language,
                    remember(info.languageTag, locale) {
                        Locale.forLanguageTag(info.languageTag).getDisplayLanguage(locale)
                    }
                )
                InfoRow(R.string.system_label_time_zone, info.timeZoneId)
                InfoRow(R.string.system_label_root_access, if (info.isRooted) yes else no)
                UptimeRow(liveUptimeMillis, fallbackMillis = info.uptimeMillis)
                InfoRow(R.string.system_label_system_as_root, if (info.isSystemAsRoot) yes else no)
                InfoRow(
                    R.string.system_label_seamless_updates,
                    stringResource(
                        if (info.isSeamlessUpdateSupported) R.string.common_supported else R.string.common_not_supported
                    )
                )
                InfoRow(R.string.system_label_dynamic_partitions, if (info.isDynamicPartitionsEnabled) enabled else disabled)
                InfoRow(R.string.system_label_project_treble, if (info.isTrebleEnabled) enabled else disabled)
            }
        }

        item(key = "runtime") {
            PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                SectionTitle(title = R.string.system_section_runtime, icon = Icons.Outlined.Settings, accentColor = AntarBlue)
                val vmName = info.vmName
                val vmVersion = info.vmVersion
                InfoRow(
                    R.string.system_label_java_vm,
                    if (vmName != null && vmVersion != null) {
                        stringResource(R.string.system_value_vm, vmName, vmVersion)
                    } else {
                        vmName ?: vmVersion
                    }
                )
                val context = LocalContext.current
                InfoRow(
                    R.string.system_label_java_vm_heap,
                    remember(info.vmMaxHeapBytes, locale) { Formatter.formatFileSize(context, info.vmMaxHeapBytes) }
                )
                InfoRow(R.string.system_label_kernel_architecture, info.kernelArchitecture)
                InfoRow(R.string.system_label_kernel_version, info.kernelVersion, singleLine = false)
                InfoRow(R.string.system_label_opengl_es, info.openGlEsVersion)
                InfoRow(
                    R.string.system_label_selinux,
                    info.selinuxMode?.let { mode ->
                        stringResource(
                            when (mode) {
                                SelinuxMode.ENFORCING -> R.string.system_selinux_enforcing
                                SelinuxMode.PERMISSIVE -> R.string.system_selinux_permissive
                                SelinuxMode.DISABLED -> R.string.common_disabled
                            }
                        )
                    }
                )
                InfoRow(R.string.system_label_ssl_provider, info.sslProvider, singleLine = false)
            }
        }

        info.widevine?.let { drm ->
            item(key = "drm") {
                DrmCard(drm = drm, yes = yes, no = no, modifier = Modifier.staggeredEntry(3))
            }
        }
    }
}

@Composable
private fun DrmCard(drm: WidevineInfo, yes: String, no: String, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier) {
        SectionTitle(title = R.string.system_section_drm, icon = Icons.Outlined.Security, accentColor = AntarPurple)
        InfoRow(R.string.system_label_drm_vendor, drm.vendor)
        InfoRow(R.string.system_label_drm_version, drm.version)
        InfoRow(R.string.system_label_drm_description, drm.description, singleLine = false)
        InfoRow(R.string.system_label_drm_algorithms, drm.algorithms)
        InfoRow(R.string.system_label_drm_security_level, drm.securityLevel)
        InfoRow(R.string.system_label_drm_system_id, drm.systemId)
        InfoRow(R.string.system_label_drm_hdcp_level, drm.hdcpLevel)
        InfoRow(R.string.system_label_drm_max_hdcp_level, drm.maxHdcpLevel)
        InfoRow(R.string.system_label_drm_usage_reporting, drm.usageReportingSupported?.let { if (it) yes else no })
        InfoRow(R.string.system_label_drm_max_sessions, drm.maxSessionCount?.toString())
        InfoRow(R.string.system_label_drm_open_sessions, drm.openSessionCount?.toString())
    }
}

/** Collects the ticking uptime here so only this row recomposes every second. */
@Composable
private fun UptimeRow(liveUptimeMillis: StateFlow<Long?>, fallbackMillis: Long) {
    val live by liveUptimeMillis.collectAsStateWithLifecycle()
    val totalSeconds = (live ?: fallbackMillis) / MILLIS_PER_SECOND
    InfoRow(
        R.string.system_label_uptime,
        stringResource(
            R.string.system_value_uptime,
            totalSeconds / SECONDS_PER_DAY,
            (totalSeconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR,
            (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE,
            totalSeconds % SECONDS_PER_MINUTE
        )
    )
}

@Composable
private fun currentLocale(): Locale {
    val locales = LocalConfiguration.current.locales
    return if (locales.size() == 0) Locale.getDefault() else locales[0]
}

/** Dessert or code name of each API level from minSdk 24; proper names, kept out of translation. */
@StringRes
private fun versionNameRes(apiLevel: Int): Int? = when (apiLevel) {
    24, 25 -> R.string.system_version_name_nougat
    26, 27 -> R.string.system_version_name_oreo
    28 -> R.string.system_version_name_pie
    29 -> R.string.system_version_name_q
    30 -> R.string.system_version_name_r
    31 -> R.string.system_version_name_s
    32 -> R.string.system_version_name_s_v2
    33 -> R.string.system_version_name_tiramisu
    34 -> R.string.system_version_name_upside_down_cake
    35 -> R.string.system_version_name_vanilla_ice_cream
    36 -> R.string.system_version_name_baklava
    else -> null
}

private fun formatDate(date: CalendarDate, locale: Locale): String {
    val calendar = Calendar.getInstance().apply {
        clear()
        set(date.year, date.month - 1, date.day)
    }
    return DateFormat.getDateInstance(DateFormat.LONG, locale).format(calendar.time)
}

private fun formatDateTime(millis: Long, locale: Locale): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale).format(Date(millis))

private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3_600L
private const val SECONDS_PER_DAY = 86_400L
