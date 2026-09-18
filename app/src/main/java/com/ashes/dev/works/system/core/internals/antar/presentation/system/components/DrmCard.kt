package com.ashes.dev.works.system.core.internals.antar.presentation.system.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.domain.model.WidevineInfo
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle

@Composable
internal fun DrmCard(drm: WidevineInfo, yes: String, no: String, modifier: Modifier = Modifier) {
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
