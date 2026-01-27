package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SystemViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SystemScreen(viewModel: SystemViewModel = koinViewModel()) {
    val system = viewModel.getSystem()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            // Header Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Android ${system.androidVersion}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = system.codename,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Operating System Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Operating System",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Version name", system.versionName)
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
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Runtime & Kernel Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Runtime & Kernel",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    InfoRow("Java Runtime", system.javaRuntime)
                    InfoRow("Java VM", system.javaVm)
                    InfoRow("Java VM stack size", system.javaVmStackSize)
                    InfoRow("Kernel architecture", system.kernelArchitecture)
                    InfoRow("Kernel version", system.kernelVersion, singleLine = false)
                    InfoRow("OpenGL ES", system.openGlEs)
                    InfoRow("SELinux", system.selinux)
                    InfoRow("OpenSSL Version", system.openSslVersion)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // DRM Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DRM",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
}
