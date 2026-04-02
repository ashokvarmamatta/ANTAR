package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.domain.model.System
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarGreen
import com.ashes.dev.works.system.core.internals.antar.presentation.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SystemViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SystemScreen(viewModel: SystemViewModel = koinViewModel()) {
    val system = viewModel.getSystem()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GradientHeaderCard {
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
                        if (system.androidVersion.isNotBlank() && system.androidVersion != "- - -") {
                            Text(
                                text = "Android ${system.androidVersion}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val displayName = system.versionName
                            .removePrefix("Android ${system.androidVersion} ")
                            .removePrefix("Android ").trim()
                        if (displayName.isNotBlank() && displayName != "- - -") {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AntarCyan
                            )
                        }
                        if (system.codename.isNotBlank() && system.codename != "- - -" && system.codename != "REL") {
                            Text(
                                text = system.codename,
                                style = MaterialTheme.typography.bodySmall,
                                color = AntarGray
                            )
                        }
                        if (system.releaseDate.isNotBlank() && system.releaseDate != "- - -") {
                            Text(
                                text = "Released ${system.releaseDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AntarGray
                            )
                        }
                    }
                }
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Operating System", icon = Icons.Outlined.Settings)
                InfoRow("Android version", system.androidVersion)
                val displayVersionName = system.versionName
                    .removePrefix("Android ${system.androidVersion} ")
                    .removePrefix("Android ").trim()
                InfoRow("Version name", displayVersionName)
                InfoRow("API Level", system.apiLevel)
                InfoRow("Build number", system.buildNumber, singleLine = false)
                InfoRow("Build Time", system.buildTime)
                InfoRow("Build ID", system.buildId, singleLine = false)
                InfoRow("Security patch level", system.securityPatchLevel)
                InfoRow("Baseband", system.baseband, singleLine = false)
                InfoRow("Language", system.language)
                InfoRow("Time zone", system.timeZone)
                InfoRow("Root access", system.rootAccess)
                InfoRow("System uptime", system.systemUptime)
                InfoRow("System-as-Root", system.systemAsRoot)
                InfoRow("Seamless updates", system.seamlessUpdates)
                InfoRow("Dynamic partitions", system.dynamicPartitions)
                InfoRow("Project Treble", system.projectTreble)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "Runtime & Kernel", icon = Icons.Outlined.Settings, accentColor = AntarBlue)
                InfoRow("Java Runtime", system.javaRuntime)
                InfoRow("Java VM", system.javaVm)
                InfoRow("Java VM stack size", system.javaVmStackSize)
                InfoRow("Kernel architecture", system.kernelArchitecture)
                InfoRow("Kernel version", system.kernelVersion, singleLine = false)
                InfoRow("OpenGL ES", system.openGlEs)
                InfoRow("SELinux", system.selinux)
                InfoRow("OpenSSL Version", system.openSslVersion, singleLine = false)
            }
        }

        item {
            PremiumCard {
                SectionTitle(title = "DRM", icon = Icons.Outlined.Security, accentColor = AntarPurple)
                InfoRow("Vendor", system.drmVendor)
                InfoRow("Version", system.drmVersion)
                InfoRow("Description", system.drmDescription, singleLine = false)
                InfoRow("Algorithm", system.drmAlgorithm)
                InfoRow("Security level", system.drmSecurityLevel)
                InfoRow("System id", system.drmSystemId)
                InfoRow("HDCP level", system.drmHdcpLevel)
                InfoRow("Max HDCP level", system.drmMaxHdcpLevel)
                InfoRow("Usage reporting support", system.drmUsageReportingSupport)
                InfoRow("Max Number of sessions", system.drmMaxNumberOfSessions)
                InfoRow("Number of open sessions", system.drmNumberOfOpenSessions)
            }
        }
    }
}
