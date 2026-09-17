package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ashes.dev.works.system.core.internals.antar.core.common.AppResult
import com.ashes.dev.works.system.core.internals.antar.core.common.appResultOf
import com.ashes.dev.works.system.core.internals.antar.domain.model.ActiveConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.ConnectionType
import com.ashes.dev.works.system.core.internals.antar.domain.model.PhoneType
import com.ashes.dev.works.system.core.internals.antar.domain.model.SimInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.TelephonyInfo
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiConnection
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiSecurity
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiStandard
import com.ashes.dev.works.system.core.internals.antar.domain.model.WifiState
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.Inet6Address

class NetworkRepositoryImpl(
    private val context: Context,
    private val io: CoroutineDispatcher
) : NetworkRepository {

    override fun observeActiveConnection(): Flow<ActiveConnection> = callbackFlow {
        val connectivity = connectivityManager()

        // All callbacks arrive on one ConnectivityManager thread, after this initial read.
        var currentNetwork: Network? = connectivity.activeNetwork
        var capabilities: NetworkCapabilities? = currentNetwork?.let { connectivity.getNetworkCapabilities(it) }
        var linkProperties: LinkProperties? = currentNetwork?.let { connectivity.getLinkProperties(it) }
        trySend(buildConnection(capabilities, linkProperties))

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                if (network != currentNetwork) {
                    currentNetwork = network
                    linkProperties = connectivity.getLinkProperties(network)
                }
                capabilities = networkCapabilities
                trySend(buildConnection(capabilities, linkProperties))
            }

            override fun onLinkPropertiesChanged(network: Network, newLinkProperties: LinkProperties) {
                if (network != currentNetwork) {
                    currentNetwork = network
                    capabilities = connectivity.getNetworkCapabilities(network)
                }
                linkProperties = newLinkProperties
                trySend(buildConnection(capabilities, linkProperties))
            }

            override fun onLost(network: Network) {
                currentNetwork = null
                capabilities = null
                linkProperties = null
                trySend(ActiveConnection.NONE)
            }
        }
        connectivity.registerDefaultNetworkCallback(callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged().flowOn(io)

    override fun observeWifiState(): Flow<WifiState> = callbackFlow {
        val connectivity = connectivityManager()
        val wifi = wifiManager()

        val activeCapabilities = connectivity.activeNetwork?.let { connectivity.getNetworkCapabilities(it) }
        val initial = activeCapabilities
            ?.takeIf { it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) }
            ?.let { wifiConnection(it) }
        trySend(WifiState(isEnabled = wifi.isWifiEnabled, connection = initial))

        val listener = object : WifiCallbackListener {
            override fun onChanged(capabilities: NetworkCapabilities) {
                trySend(WifiState(isEnabled = wifi.isWifiEnabled, connection = wifiConnection(capabilities)))
            }

            override fun onLost() {
                trySend(WifiState(isEnabled = wifi.isWifiEnabled, connection = null))
            }
        }
        val callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationAwareWifiCallback(listener)
        } else {
            plainWifiCallback(listener)
        }
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivity.registerNetworkCallback(request, callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged().flowOn(io)

    override suspend fun getTelephonyInfo(): AppResult<TelephonyInfo> = withContext(io) {
        appResultOf {
            // Only unprivileged TelephonyManager APIs (no READ_PHONE_STATE): the default SIM's operator,
            // MCC/MNC, country, carrier id and roaming. Per-slot SubscriptionInfo needs READ_PHONE_STATE,
            // so a second SIM is not reported at all.
            val telephony = context.getSystemService(TelephonyManager::class.java)
                ?: throw UnsupportedOperationException()
            TelephonyInfo(
                isMobileDataConnected = runCatching { telephony.dataState == TelephonyManager.DATA_CONNECTED }.getOrNull(),
                modemCount = modemCount(telephony),
                phoneType = when (telephony.phoneType) {
                    TelephonyManager.PHONE_TYPE_GSM -> PhoneType.GSM
                    TelephonyManager.PHONE_TYPE_CDMA -> PhoneType.CDMA
                    TelephonyManager.PHONE_TYPE_SIP -> PhoneType.SIP
                    else -> PhoneType.NONE
                },
                sim = if (telephony.simState == TelephonyManager.SIM_STATE_READY) simInfo(telephony) else null
            )
        }
    }

    // ── Default network ──────────────────────────────────────────────

    private fun buildConnection(capabilities: NetworkCapabilities?, linkProperties: LinkProperties?): ActiveConnection {
        if (capabilities == null) return ActiveConnection.NONE
        val type = connectionTypeOf(capabilities)
        val addresses = linkProperties?.linkAddresses.orEmpty()
        val ipv4 = addresses.firstOrNull { it.address is Inet4Address }
        val legacyDhcp = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && type == ConnectionType.WIFI) legacyDhcpInfo() else null

        return ActiveConnection(
            type = type,
            interfaceName = linkProperties?.interfaceName,
            ipv4Address = ipv4?.address?.hostAddress,
            ipv4PrefixLength = ipv4?.prefixLength,
            netmask = ipv4?.prefixLength?.let { prefixToNetmask(it) },
            ipv6Addresses = addresses.filter { it.address is Inet6Address }.mapNotNull { it.address.hostAddress },
            gateway = defaultGateway(linkProperties),
            dnsServers = linkProperties?.dnsServers.orEmpty().mapNotNull { it.hostAddress },
            dhcpServer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                linkProperties?.dhcpServerAddress?.hostAddress
            } else {
                legacyDhcp?.serverAddress?.takeIf { it != 0 }?.let { intToIp(it) }
            },
            dhcpLeaseSeconds = legacyDhcp?.leaseDuration?.takeIf { it > 0 }
        )
    }

    private fun connectionTypeOf(capabilities: NetworkCapabilities): ConnectionType = when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> ConnectionType.BLUETOOTH
        else -> ConnectionType.OTHER
    }

    /** Gateway of the default route, preferring IPv4 so it lines up with the netmask shown. */
    private fun defaultGateway(linkProperties: LinkProperties?): String? {
        val gateways = linkProperties?.routes.orEmpty()
            .filter { it.isDefaultRoute }
            .mapNotNull { it.gateway }
            .filterNot { it.isAnyLocalAddress }
        return (gateways.firstOrNull { it is Inet4Address } ?: gateways.firstOrNull())?.hostAddress
    }

    /** Legacy Wi-Fi DHCP details; only called below Android 12, where there is no replacement for the lease. */
    @Suppress("DEPRECATION")
    private fun legacyDhcpInfo(): DhcpInfo? = runCatching { wifiManager().dhcpInfo }.getOrNull()

    // ── Wi-Fi ────────────────────────────────────────────────────────

    private interface WifiCallbackListener {
        fun onChanged(capabilities: NetworkCapabilities)
        fun onLost()
    }

    /** Android 12+: the flag makes the platform include SSID/BSSID in [WifiInfo] when location access allows it. */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun locationAwareWifiCallback(listener: WifiCallbackListener): ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO) {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) =
                listener.onChanged(networkCapabilities)

            override fun onLost(network: Network) = listener.onLost()
        }

    private fun plainWifiCallback(listener: WifiCallbackListener): ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) =
                listener.onChanged(networkCapabilities)

            override fun onLost(network: Network) = listener.onLost()
        }

    private fun wifiConnection(capabilities: NetworkCapabilities): WifiConnection? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (capabilities.transportInfo as? WifiInfo)?.let { modernWifiConnection(it) }
        } else {
            legacyWifiConnection()
        }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun modernWifiConnection(info: WifiInfo): WifiConnection = WifiConnection(
        rssiDbm = info.rssi.takeIf { it > INVALID_RSSI },
        linkSpeedMbps = info.linkSpeed.takeIf { it != WifiInfo.LINK_SPEED_UNKNOWN },
        frequencyMhz = info.frequency.takeIf { it > 0 },
        standard = wifiStandardOf(info.wifiStandard),
        ssid = ssidOf(info.ssid),
        bssid = bssidOf(info.bssid),
        security = securityOf(info.currentSecurityType)
    )

    /** Below Android 12 only: the deprecated connection info and a scan-result match for security. */
    @Suppress("DEPRECATION")
    private fun legacyWifiConnection(): WifiConnection? {
        val info = runCatching { wifiManager().connectionInfo }.getOrNull() ?: return null
        val bssid = bssidOf(info.bssid)
        return WifiConnection(
            rssiDbm = info.rssi.takeIf { it > INVALID_RSSI },
            linkSpeedMbps = info.linkSpeed.takeIf { it != WifiInfo.LINK_SPEED_UNKNOWN },
            frequencyMhz = info.frequency.takeIf { it > 0 },
            standard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) wifiStandardOf(info.wifiStandard) else null,
            ssid = ssidOf(info.ssid),
            bssid = bssid,
            security = bssid?.let { legacySecurityOf(it) }
        )
    }

    /** Scan results are only returned to apps holding location access, so check before asking. */
    @Suppress("DEPRECATION")
    private fun legacySecurityOf(bssid: String): WifiSecurity? {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return null
        val capabilities = runCatching { wifiManager().scanResults }.getOrNull()
            ?.firstOrNull { it.BSSID == bssid }
            ?.capabilities
            ?: return null
        return when {
            capabilities.contains("SAE") || capabilities.contains("WPA3") -> WifiSecurity.WPA3_PERSONAL
            capabilities.contains("OWE") -> WifiSecurity.ENHANCED_OPEN
            capabilities.contains("EAP") -> WifiSecurity.ENTERPRISE
            capabilities.contains("WPA2") || capabilities.contains("RSN") -> WifiSecurity.WPA2_PERSONAL
            capabilities.contains("WPA") -> WifiSecurity.WPA_PERSONAL
            capabilities.contains("WEP") -> WifiSecurity.WEP
            else -> WifiSecurity.OPEN
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun securityOf(type: Int): WifiSecurity? = when (type) {
        WifiInfo.SECURITY_TYPE_UNKNOWN -> null
        WifiInfo.SECURITY_TYPE_OPEN -> WifiSecurity.OPEN
        WifiInfo.SECURITY_TYPE_WEP -> WifiSecurity.WEP
        WifiInfo.SECURITY_TYPE_PSK -> WifiSecurity.WPA_WPA2_PERSONAL
        WifiInfo.SECURITY_TYPE_SAE -> WifiSecurity.WPA3_PERSONAL
        WifiInfo.SECURITY_TYPE_OWE -> WifiSecurity.ENHANCED_OPEN
        WifiInfo.SECURITY_TYPE_EAP -> WifiSecurity.ENTERPRISE
        WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE -> WifiSecurity.WPA3_ENTERPRISE
        WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT -> WifiSecurity.WPA3_ENTERPRISE_192_BIT
        WifiInfo.SECURITY_TYPE_WAPI_PSK -> WifiSecurity.WAPI_PERSONAL
        WifiInfo.SECURITY_TYPE_WAPI_CERT -> WifiSecurity.WAPI_ENTERPRISE
        WifiInfo.SECURITY_TYPE_PASSPOINT_R1_R2,
        WifiInfo.SECURITY_TYPE_PASSPOINT_R3 -> WifiSecurity.PASSPOINT
        WifiInfo.SECURITY_TYPE_OSEN -> WifiSecurity.OSEN
        else -> WifiSecurity.OTHER
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun wifiStandardOf(standard: Int): WifiStandard? = when (standard) {
        ScanResult.WIFI_STANDARD_LEGACY -> WifiStandard.LEGACY
        ScanResult.WIFI_STANDARD_11N -> WifiStandard.WIFI_4
        ScanResult.WIFI_STANDARD_11AC -> WifiStandard.WIFI_5
        ScanResult.WIFI_STANDARD_11AX -> WifiStandard.WIFI_6
        ScanResult.WIFI_STANDARD_11AD -> WifiStandard.WIGIG
        WIFI_STANDARD_11BE -> WifiStandard.WIFI_7
        else -> null
    }

    /** SSIDs arrive quoted; without location access the platform substitutes a placeholder. */
    private fun ssidOf(raw: String?): String? =
        raw?.takeUnless { it == UNKNOWN_SSID }?.removeSurrounding("\"")?.takeIf { it.isNotBlank() }

    private fun bssidOf(raw: String?): String? = raw?.takeUnless { it == REDACTED_BSSID }?.takeIf { it.isNotBlank() }

    // ── Telephony ────────────────────────────────────────────────────

    private fun modemCount(telephony: TelephonyManager): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            telephony.activeModemCount
        } else {
            @Suppress("DEPRECATION")
            telephony.phoneCount
        }

    private fun simInfo(telephony: TelephonyManager): SimInfo {
        val simOperator = telephony.simOperator.orEmpty()
        val networkOperatorName = telephony.networkOperatorName?.takeIf { it.isNotBlank() }
        return SimInfo(
            operatorName = telephony.simOperatorName?.takeIf { it.isNotBlank() } ?: networkOperatorName,
            countryIso = telephony.simCountryIso?.takeIf { it.isNotBlank() },
            mcc = simOperator.takeIf { it.length >= 3 }?.substring(0, 3),
            mnc = simOperator.takeIf { it.length > 3 }?.substring(3),
            carrierId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                telephony.simCarrierId.takeIf { it != TelephonyManager.UNKNOWN_CARRIER_ID }
            } else {
                null
            },
            networkOperatorName = networkOperatorName,
            isRoaming = telephony.isNetworkRoaming
        )
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun connectivityManager(): ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java) ?: throw UnsupportedOperationException()

    private fun wifiManager(): WifiManager =
        context.applicationContext.getSystemService(WifiManager::class.java) ?: throw UnsupportedOperationException()

    private fun prefixToNetmask(prefixLength: Int): String {
        val mask = if (prefixLength <= 0) 0L else (0xFFFFFFFFL shl (32 - prefixLength.coerceAtMost(32))) and 0xFFFFFFFFL
        return listOf(24, 16, 8, 0).joinToString(".") { shift -> ((mask shr shift) and 0xFF).toString() }
    }

    /** Legacy DhcpInfo addresses are little-endian ints. */
    private fun intToIp(address: Int): String =
        listOf(0, 8, 16, 24).joinToString(".") { shift -> ((address shr shift) and 0xFF).toString() }

    private companion object {
        const val INVALID_RSSI = -127
        const val UNKNOWN_SSID = "<unknown ssid>"
        const val REDACTED_BSSID = "02:00:00:00:00:00"

        /** ScanResult.WIFI_STANDARD_11BE, added in Android 13; inlined so older SDK stubs still resolve. */
        const val WIFI_STANDARD_11BE = 8
    }
}
