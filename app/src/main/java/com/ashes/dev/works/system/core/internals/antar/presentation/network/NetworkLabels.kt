package com.ashes.dev.works.system.core.internals.antar.presentation.network

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SettingsEthernet
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.domain.model.ConnectionType
import com.ashes.dev.works.system.core.internals.antar.domain.model.PhoneType
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiSecurity
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiStandard

@StringRes
internal fun ConnectionType.labelRes(): Int = when (this) {
    ConnectionType.WIFI -> R.string.network_type_wifi
    ConnectionType.CELLULAR -> R.string.network_type_cellular
    ConnectionType.ETHERNET -> R.string.network_type_ethernet
    ConnectionType.VPN -> R.string.network_type_vpn
    ConnectionType.BLUETOOTH -> R.string.network_type_bluetooth
    ConnectionType.OTHER -> R.string.network_type_other
    ConnectionType.NONE -> R.string.network_type_none
}

internal fun ConnectionType.icon(): ImageVector = when (this) {
    ConnectionType.WIFI -> Icons.Outlined.Wifi
    ConnectionType.CELLULAR -> Icons.Outlined.CellTower
    ConnectionType.ETHERNET -> Icons.Outlined.SettingsEthernet
    ConnectionType.VPN -> Icons.Outlined.VpnKey
    ConnectionType.BLUETOOTH -> Icons.Outlined.Bluetooth
    ConnectionType.OTHER -> Icons.Outlined.Public
    ConnectionType.NONE -> Icons.Outlined.WifiOff
}

@StringRes
internal fun WifiSecurity.labelRes(): Int = when (this) {
    WifiSecurity.OPEN -> R.string.network_security_open
    WifiSecurity.WEP -> R.string.network_security_wep
    WifiSecurity.WPA_PERSONAL -> R.string.network_security_wpa_personal
    WifiSecurity.WPA2_PERSONAL -> R.string.network_security_wpa2_personal
    WifiSecurity.WPA_WPA2_PERSONAL -> R.string.network_security_wpa_wpa2_personal
    WifiSecurity.WPA3_PERSONAL -> R.string.network_security_wpa3_personal
    WifiSecurity.ENHANCED_OPEN -> R.string.network_security_enhanced_open
    WifiSecurity.ENTERPRISE -> R.string.network_security_enterprise
    WifiSecurity.WPA3_ENTERPRISE -> R.string.network_security_wpa3_enterprise
    WifiSecurity.WPA3_ENTERPRISE_192_BIT -> R.string.network_security_wpa3_enterprise_192
    WifiSecurity.WAPI_PERSONAL -> R.string.network_security_wapi_personal
    WifiSecurity.WAPI_ENTERPRISE -> R.string.network_security_wapi_enterprise
    WifiSecurity.PASSPOINT -> R.string.network_security_passpoint
    WifiSecurity.OSEN -> R.string.network_security_osen
    WifiSecurity.OTHER -> R.string.network_security_other
}

@StringRes
internal fun WifiStandard.labelRes(): Int = when (this) {
    WifiStandard.LEGACY -> R.string.network_wifi_standard_legacy
    WifiStandard.WIFI_4 -> R.string.network_wifi_standard_4
    WifiStandard.WIFI_5 -> R.string.network_wifi_standard_5
    WifiStandard.WIFI_6 -> R.string.network_wifi_standard_6
    WifiStandard.WIGIG -> R.string.network_wifi_standard_wigig
    WifiStandard.WIFI_7 -> R.string.network_wifi_standard_7
}

@StringRes
internal fun PhoneType.labelRes(): Int = when (this) {
    PhoneType.NONE -> R.string.network_phone_type_none
    PhoneType.GSM -> R.string.network_phone_type_gsm
    PhoneType.CDMA -> R.string.network_phone_type_cdma
    PhoneType.SIP -> R.string.network_phone_type_sip
}
