package com.guodong.android.system.permission.adapter.rockchips

import androidx.annotation.Keep
import com.guodong.android.system.permission.AospSystemPermission
import com.guodong.android.system.permission.adapter.rockchips.ethernet.EthernetCompat
import com.guodong.android.system.permission.domain.NetworkAddress

/**
 * Created by john.wick on 2025/8/5
 */
@Keep
open class RockChipsSystemPermission : AospSystemPermission() {

    companion object {
        private const val TAG = "RockChipsSystemPermission"
    }

    override suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean {
        return EthernetCompat.setStaticAddress(context, ipAddress, netmask, gateway, dns1, dns2)
    }

    override suspend fun setEthernetDhcpAddress(): Boolean {
        return EthernetCompat.setDhcpAddress(context)
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return EthernetCompat.getNetworkAddress(context)
    }

    override suspend fun getEthernetMacAddress(): String {
        return EthernetCompat.getMacAddress()
    }
}