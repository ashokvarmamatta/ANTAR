package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
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

        return Network(
            connectionType = activeNetwork?.typeName ?: "- - -",
            statusDescription = activeNetwork?.state?.name ?: "- - -",
            signalStrength = wifiInfo.rssi.toString(),
            wifiStatus = if (wifiManager.isWifiEnabled) "Enabled" else "Disabled",
            wifiSafety = "- - -",
            bssid = wifiInfo.bssid ?: "- - -",
            dhcp = "- - -",
            dhcpLeaseDuration = "- - -",
            gateway = "- - -",
            netmask = "- - -",
            dns1 = "- - -",
            dns2 = "- - -",
            ip = "- - -",
            ipv6 = "- - -",
            wifiInterface = "- - -",
            linkSpeed = "${wifiInfo.linkSpeed} Mbps",
            frequency = "${wifiInfo.frequency} MHz",
            wifiFeatures = "- - -",
            mobileDataStatus = if (telephonyManager.dataState == TelephonyManager.DATA_CONNECTED) "Connected" else "Disconnected",
            multiSim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) telephonyManager.phoneCount.toString() else "1",
            deviceType = "- - -",
            sim1Name = telephonyManager.simOperatorName ?: "- - -",
            sim1PhoneNumber = "- - -",
            sim1CountryIso = telephonyManager.simCountryIso ?: "- - -",
            sim1Mcc = if ((telephonyManager.simOperator?.length ?: 0) >= 3) telephonyManager.simOperator?.substring(0, 3) ?: "- - -" else "- - -",
            sim1Mnc = if ((telephonyManager.simOperator?.length ?: 0) > 3) telephonyManager.simOperator?.substring(3) ?: "- - -" else "- - -",
            sim1CarrierId = "- - -",
            sim1CarrierName = telephonyManager.simOperatorName ?: "- - -",
            sim1DataRoaming = if (telephonyManager.isNetworkRoaming) "Enabled" else "Disabled",
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
}
