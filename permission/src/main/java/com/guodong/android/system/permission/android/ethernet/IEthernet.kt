package com.guodong.android.system.permission.android.ethernet

import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.hidden.compat.EthernetManagerApis

/**
 * Created by john.wick on 2025/7/2
 */
internal interface IEthernet {

    fun setStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean = try {
        EthernetManagerApis.setStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
        true
    } catch (e: Exception) {
        false
    }

    /**
     * 开启以太网DHCP
     */
    fun setDhcpAddress(): Boolean = try {
        EthernetManagerApis.setDhcpAddress()
        true
    } catch (e: Exception) {
        false
    }

    /**
     * 获取以太网网络信息
     */
    suspend fun getNetworkAddress(): NetworkAddress = try {
        val address = EthernetManagerApis.getNetworkAddress()
        NetworkAddress(
            address.ipAssignment,
            address.address,
            address.netmask,
            address.gateway,
            address.dns1,
            address.dns2
        )
    } catch (e: Exception) {
        NetworkAddress.UNASSIGNED
    }

    /**
     * 获取以太网MAC地址
     */
    suspend fun getMacAddress(): String = withContext(Dispatchers.IO) {
        EthernetManagerApis.getMacAddress().orEmpty()
    }
}