package com.guodong.android.system.permission.android.ethernet

import com.guodong.android.system.permission.domain.NetworkAddress

/**
 * Created by john.wick on 2025/7/2
 */
internal object EthernetCompat : IEthernet {

    override fun setStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ): Boolean {
        return super.setStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
    }

    override fun setDhcpAddress(): Boolean {
        return super.setDhcpAddress()
    }

    override suspend fun getNetworkAddress(): NetworkAddress {
        return super.getNetworkAddress()
    }

    override suspend fun getMacAddress(): String {
        return super.getMacAddress()
    }
}