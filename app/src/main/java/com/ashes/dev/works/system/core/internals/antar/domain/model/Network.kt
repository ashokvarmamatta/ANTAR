package com.ashes.dev.works.system.core.internals.antar.domain.model

/** Transport of the network the system currently routes traffic over. */
enum class ConnectionType { WIFI, CELLULAR, ETHERNET, VPN, BLUETOOTH, OTHER, NONE }

/** Security of the connected Wi-Fi network. Values the platform could not tell apart share one constant. */
enum class WifiSecurity {
    OPEN,
    WEP,
    WPA_PERSONAL,
    WPA2_PERSONAL,
    /** Android 12+ reports WPA and WPA2 pre-shared key networks as one type. */
    WPA_WPA2_PERSONAL,
    WPA3_PERSONAL,
    ENHANCED_OPEN,
    ENTERPRISE,
    WPA3_ENTERPRISE,
    WPA3_ENTERPRISE_192_BIT,
    WAPI_PERSONAL,
    WAPI_ENTERPRISE,
    PASSPOINT,
    OSEN,
    OTHER
}

/** IEEE 802.11 generation of the current Wi-Fi link. */
enum class WifiStandard { LEGACY, WIFI_4, WIFI_5, WIFI_6, WIGIG, WIFI_7 }

enum class PhoneType { NONE, GSM, CDMA, SIP }

/**
 * The default network, read without any runtime permission. Addresses are textual IP literals
 * (technical tokens, not prose); null means the platform did not report the value.
 */
data class ActiveConnection(
    val type: ConnectionType,
    val interfaceName: String?,
    val ipv4Address: String?,
    val ipv4PrefixLength: Int?,
    /** Dotted netmask derived from [ipv4PrefixLength]. */
    val netmask: String?,
    val ipv6Addresses: List<String>,
    val gateway: String?,
    val dnsServers: List<String>,
    val dhcpServer: String?,
    /** Only reported below Android 12, where the legacy DHCP info is still available. */
    val dhcpLeaseSeconds: Int?
) {
    companion object {
        val NONE = ActiveConnection(
            type = ConnectionType.NONE,
            interfaceName = null,
            ipv4Address = null,
            ipv4PrefixLength = null,
            netmask = null,
            ipv6Addresses = emptyList(),
            gateway = null,
            dnsServers = emptyList(),
            dhcpServer = null,
            dhcpLeaseSeconds = null
        )
    }
}

/**
 * The connected Wi-Fi link. Signal facts need no permission; [ssid], [bssid] and [security] are
 * null unless the app holds precise location and location services are on.
 */
data class WifiConnection(
    val rssiDbm: Int?,
    val linkSpeedMbps: Int?,
    val frequencyMhz: Int?,
    val standard: WifiStandard?,
    val ssid: String?,
    val bssid: String?,
    val security: WifiSecurity?
)

data class WifiState(
    val isEnabled: Boolean,
    /** Null while not connected to a Wi-Fi network. */
    val connection: WifiConnection?
)

/** The default SIM, read only from unprivileged telephony APIs (no READ_PHONE_STATE). */
data class SimInfo(
    val operatorName: String?,
    val countryIso: String?,
    val mcc: String?,
    val mnc: String?,
    val carrierId: Int?,
    val networkOperatorName: String?,
    val isRoaming: Boolean
)

data class TelephonyInfo(
    /** Null when the platform refuses to report the data state. */
    val isMobileDataConnected: Boolean?,
    val modemCount: Int,
    val phoneType: PhoneType,
    /** Null when no ready SIM is inserted. */
    val sim: SimInfo?
)
