package com.guodong.android.system.permission.adapter.rockchips.ethernet

import android.content.Context
import android.net.IEthernetManager
import android.os.Build
import com.guodong.android.system.permission.domain.NetworkAddress

/**
 * Created by john.wick on 2025/7/2
 */
internal object EthernetCompat : IEthernet {

    private val ethernet: IEthernet = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> EthernetApi33
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> EthernetApi28
        else -> EthernetApi25
    }

    override fun setStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ): Boolean {
        return ethernet.setStaticAddress(context, ipAddress, netmask, gateway, dns1, dns2)
    }

    override fun getEthernetManager(): IEthernetManager {
        return ethernet.getEthernetManager()
    }

    override fun setDhcpAddress(context: Context): Boolean {
        return ethernet.setDhcpAddress(context)
    }

    override suspend fun getNetworkAddress(context: Context): NetworkAddress {
        return ethernet.getNetworkAddress(context)
    }

    override suspend fun getDhcpNetworkAddress(context: Context): NetworkAddress {
        return ethernet.getDhcpNetworkAddress(context)
    }

    override suspend fun getStaticNetworkAddress(context: Context): NetworkAddress {
        return ethernet.getStaticNetworkAddress(context)
    }

    override suspend fun getMacAddress(): String {
        return ethernet.getMacAddress()
    }
}