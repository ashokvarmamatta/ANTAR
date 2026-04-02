package com.ashes.dev.works.system.core.internals.antar.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.System
import com.ashes.dev.works.system.core.internals.antar.presentation.viewmodel.SystemViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SystemScreen(viewModel: SystemViewModel = koinViewModel()) {
    val system = viewModel.getSystem()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item(key = "header") {
            SystemHeader(
                androidVersion = system.androidVersion,
                codename = system.codename,
                versionName = system.versionName,
                releaseDate = system.releaseDate
            )
        }

        item(key = "os_info") { 
            Spacer(modifier = Modifier.height(16.dp))
            OperatingSystemCard(system = system)
        }

        item(key = "runtime_kernel") { 
            Spacer(modifier = Modifier.height(16.dp))
            RuntimeAndKernelCard(system = system)
        }

        item(key = "drm") { 
            Spacer(modifier = Modifier.height(16.dp))
            DrmCard(system = system)
        }
    }
}

@Composable
private fun SystemHeader(androidVersion: String, codename: String, versionName: String, releaseDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_android_version), 
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                if (androidVersion.isNotBlank() && androidVersion != "- - -") {
                    HeaderRow(icon = Icons.Default.Build, text = "Android $androidVersion")
                }
                if (codename.isNotBlank() && codename != "- - -" && codename != "REL") {
                    HeaderRow(icon = Icons.Default.CheckCircle, text = codename)
                }
                val displayName = versionName.removePrefix("Android $androidVersion ").removePrefix("Android ").trim()
                if (displayName.isNotBlank() && displayName != "- - -") {
                    HeaderRow(icon = Icons.Default.Star, text = displayName)
                }
                if (releaseDate.isNotBlank() && releaseDate != "- - -") {
                    HeaderRow(icon = Icons.Default.DateRange, text = "Released : $releaseDate")
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun OperatingSystemCard(system: System) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Operating System",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val displayVersionName = system.versionName.removePrefix("Android ${system.androidVersion} ").removePrefix("Android ").trim()

            InfoRow("Android version", system.androidVersion)
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
}

@Composable
private fun RuntimeAndKernelCard(system: System) {
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
            InfoRow("OpenSSL Version", system.openSslVersion, singleLine = false)
        }
    }
}

@Composable
private fun DrmCard(system: System) {
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
