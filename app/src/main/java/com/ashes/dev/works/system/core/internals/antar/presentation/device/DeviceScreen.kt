package com.ashes.dev.works.system.core.internals.antar.presentation.device

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.presentation.components.CopyableInfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.CopyableInfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.common.ErrorState
import com.ashes.dev.works.system.core.internals.antar.presentation.components.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.common.InfoRow
import com.ashes.dev.works.system.core.internals.antar.presentation.components.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.presentation.components.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.presentation.components.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.presentation.common.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.presentation.components.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.DeviceType
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = LocalAnimationIntensity.current.contentSwap(),
        label = "deviceState"
    ) { state ->
        when (state) {
            DeviceUiState.Loading -> LoadingSkeleton(sections = 2)
            is DeviceUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is DeviceUiState.Content -> DeviceContent(state.info)
        }
    }
}

@Composable
private fun DeviceContent(info: DeviceInfo) {
    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.device_header_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = AntarCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = info.deviceName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.device_header_manufacturer_model, info.manufacturer, info.model),
                        style = MaterialTheme.typography.bodySmall,
                        color = AntarGray
                    )
                }
            }
        }

        item(key = "general") {
            PremiumCard(modifier = Modifier.staggeredEntry(1)) {
                SectionTitle(title = R.string.device_section_general, icon = Icons.Outlined.PhoneAndroid)
                InfoRow(R.string.device_label_device_name, info.deviceName)
                InfoRow(R.string.device_label_model, info.model)
                InfoRow(R.string.device_label_manufacturer, info.manufacturer)
                InfoRow(R.string.device_label_device, info.device)
                InfoRow(R.string.device_label_board, info.board)
                InfoRow(R.string.device_label_hardware, info.hardware)
                InfoRow(R.string.device_label_brand, info.brand)
            }
        }

        item(key = "identifiers") {
            PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                SectionTitle(
                    title = R.string.device_section_identifiers,
                    icon = Icons.Outlined.Fingerprint,
                    accentColor = AntarPurple
                )
                CopyableInfoRow(R.string.device_label_android_id, info.androidId)
                if (info.isHardwareSerialRestricted) {
                    InfoRow(R.string.device_label_hardware_serial, stringResource(R.string.device_value_serial_restricted))
                } else {
                    CopyableInfoRow(R.string.device_label_hardware_serial, info.hardwareSerial)
                }
                CopyableInfoRow(R.string.device_label_build_fingerprint, info.buildFingerprint)
                InfoRow(
                    R.string.device_label_device_type,
                    stringResource(
                        when (info.deviceType) {
                            DeviceType.PHONE -> R.string.device_type_phone
                            DeviceType.TABLET -> R.string.device_type_tablet
                        }
                    )
                )
                InfoRow(R.string.device_label_network_operator, info.networkOperator)
                InfoRow(
                    R.string.device_label_mobile_data,
                    info.isMobileDataConnected?.let { connected ->
                        stringResource(if (connected) R.string.device_value_connected else R.string.device_value_disconnected)
                    }
                )
                InfoRow(
                    R.string.device_label_usb_debugging,
                    info.isUsbDebuggingEnabled?.let { enabled ->
                        stringResource(if (enabled) R.string.common_enabled else R.string.common_disabled)
                    }
                )
            }
        }
    }
}
