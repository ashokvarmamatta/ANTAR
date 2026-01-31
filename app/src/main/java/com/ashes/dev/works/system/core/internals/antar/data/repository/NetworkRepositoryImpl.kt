package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ashes.dev.works.system.core.internals.antar.domain.model.Network
import com.ashes.dev.works.system.core.internals.antar.domain.repository.NetworkRepository
import java.net.Inet4Address

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

        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionInfoList = if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                subscriptionManager.activeSubscriptionInfoList
            } catch (e: SecurityException) {
                Log.e("NetworkRepositoryImpl", "Failed to get active subscription info list", e)
                null
            }
        } else {
            Log.e("NetworkRepositoryImpl", "READ_PHONE_STATE not granted")
            null
        }

        val sim1Info = if (!subscriptionInfoList.isNullOrEmpty()) subscriptionInfoList[0] else null
        val sim2Info = if (subscriptionInfoList != null && subscriptionInfoList.size > 1) subscriptionInfoList[1] else null

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
            deviceType = try {
                getNetworkType(telephonyManager.networkType)
            } catch (e: SecurityException) {
                "Permission not granted"
            },
            sim1Name = sim1Info?.carrierName?.toString() ?: "No SIM detected",
            sim1PhoneNumber = if (context.checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                sim1Info?.number ?: "- - -"
            } else {
                "Permission not granted"
            },
            sim1CountryIso = sim1Info?.countryIso ?: "- - -",
            sim1Mcc = sim1Info?.mcc.toString(),
            sim1Mnc = sim1Info?.mnc.toString(),
            sim1CarrierId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) sim1Info?.carrierId.toString() else "- - -",
            sim1CarrierName = sim1Info?.carrierName?.toString() ?: "- - -",
            sim1DataRoaming = if (sim1Info?.dataRoaming == SubscriptionManager.DATA_ROAMING_ENABLE) "Enabled" else "Disabled",
            sim2Name = sim2Info?.carrierName?.toString() ?: "No SIM detected",
            sim2PhoneNumber = if (context.checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                sim2Info?.number ?: "- - -"
            } else {
                "Permission not granted"
            },
            sim2CountryIso = sim2Info?.countryIso ?: "- - -",
            sim2Mcc = sim2Info?.mcc.toString(),
            sim2Mnc = sim2Info?.mnc.toString(),
            sim2CarrierId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) sim2Info?.carrierId.toString() else "- - -",
            sim2CarrierName = sim2Info?.carrierName?.toString() ?: "- - -",
            sim2DataRoaming = if (sim2Info?.dataRoaming == SubscriptionManager.DATA_ROAMING_ENABLE) "Enabled" else "Disabled"
        )
    }

    private fun getWifiSecurityType(wifiManager: WifiManager): String {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Unknown (Needs Location)"
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


    private fun getNetworkType(networkType: Int): String {
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev. 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev. A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev. B"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_IDEN -> "iDen"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }

    private fun intToIp(ipAddress: Int): String {
        return (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
    }
}
