package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import com.ashes.dev.works.system.core.internals.antar.domain.model.Network
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository

class NetworkRepositoryImpl(private val context: Context) : NetworkRepository {
    override fun getNetwork(): Network {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        val activeNetwork = connectivityManager.activeNetworkInfo
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        val dhcpInfo = wifiManager.dhcpInfo

        // SIM info — sourced only from unprivileged TelephonyManager APIs (no READ_PHONE_STATE).
        // We can read default-SIM data: operator name, SIM operator (MCC+MNC), country, SIM state,
        // carrier id (API 28+) and roaming. We cannot enumerate per-slot SubscriptionInfo without
        // READ_PHONE_STATE, so SIM 2 is intentionally left empty (the UI hides empty SIM cards).
        val simState = telephonyManager.simState
        val hasSim = simState == TelephonyManager.SIM_STATE_READY

        val simOperator = telephonyManager.simOperator.orEmpty()
        val simMcc = if (simOperator.length >= 3) simOperator.substring(0, 3) else "- - -"
        val simMnc = if (simOperator.length > 3) simOperator.substring(3) else "- - -"

        val simCarrierId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            telephonyManager.simCarrierId.toString()
        } else {
            "- - -"
        }

        val sim1Name = if (hasSim) {
            telephonyManager.simOperatorName.ifBlank { telephonyManager.networkOperatorName.ifBlank { "SIM detected" } }
        } else {
            "- - -"
        }

        return Network(
            connectionType = activeNetwork?.typeName ?: "- - -",
            statusDescription = activeNetwork?.state?.name ?: "- - -",
            signalStrength = wifiInfo.rssi.toString(),
            wifiStatus = if (wifiManager.isWifiEnabled) "Enabled" else "Disabled",
            wifiSafety = getWifiSecurityType(wifiManager),
            bssid = wifiInfo.bssid ?: "- - -",
            dhcp = intToIp(dhcpInfo.serverAddress),
            dhcpLeaseDuration = dhcpInfo.leaseDuration.toString(),
            gateway = intToIp(dhcpInfo.gateway),
            netmask = intToIp(dhcpInfo.netmask),
            dns1 = intToIp(dhcpInfo.dns1),
            dns2 = intToIp(dhcpInfo.dns2),
            ip = intToIp(dhcpInfo.ipAddress),
            ipv6 = linkProperties?.linkAddresses?.filter { it.address is java.net.Inet6Address }?.joinToString { it.address.hostAddress } ?: "- - -",
            wifiInterface = linkProperties?.interfaceName ?: "- - -",
            linkSpeed = "${wifiInfo.linkSpeed} Mbps",
            frequency = "${wifiInfo.frequency} MHz",
            wifiFeatures = "- - -",
            mobileDataStatus = if (telephonyManager.dataState == TelephonyManager.DATA_CONNECTED) "Connected" else "Disconnected",
            multiSim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) telephonyManager.phoneCount.toString() else "1",
            deviceType = "- - -",
            sim1Name = sim1Name,
            sim1PhoneNumber = "- - -",
            sim1CountryIso = telephonyManager.simCountryIso.ifBlank { "- - -" },
            sim1Mcc = simMcc,
            sim1Mnc = simMnc,
            sim1CarrierId = simCarrierId,
            sim1CarrierName = if (hasSim) telephonyManager.networkOperatorName.ifBlank { "- - -" } else "- - -",
            sim1DataRoaming = if (telephonyManager.isNetworkRoaming) "Enabled" else "Disabled",
            // SIM 2: cannot be inspected without READ_PHONE_STATE — UI hides this card when blank.
            sim2Name = "- - -",
            sim2PhoneNumber = "- - -",
            sim2CountryIso = "- - -",
            sim2Mcc = "- - -",
            sim2Mnc = "- - -",
            sim2CarrierId = "- - -",
            sim2CarrierName = "- - -",
            sim2DataRoaming = "- - -"
        )
    }

    private fun getWifiSecurityType(wifiManager: WifiManager): String {
        // Wi-Fi scan results are gated on FINE_LOCATION (≤ API 32) AND NEARBY_WIFI_DEVICES (API 33+).
        // Either gate denying produces empty scan results, so we check both.
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Unknown (Needs Location)"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED
        ) {
            return "Unknown (Needs Nearby Devices)"
        }
        val scanResults = wifiManager.scanResults
        val currentWifi = wifiManager.connectionInfo

        val result = scanResults.find { it.BSSID == currentWifi.bssid }
        return when {
            result == null -> "Unknown (Needs Location)"
            result.capabilities.contains("WPA3") -> "WPA3"
            result.capabilities.contains("WPA2") -> "WPA2"
            result.capabilities.contains("WPA") -> "WPA"
            result.capabilities.contains("WEP") -> "WEP"
            else -> "Open"
        }
    }

    private fun intToIp(ipAddress: Int): String {
        return (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
    }
}
