package com.guodong.android.system.permission.ethernet

import android.content.Context
import androidx.annotation.Keep
import com.guodong.android.system.permission.annotation.EthernetState
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.flow.Flow

/**
 * Created by john.wick on 2025/7/2
 */
@Keep
interface IEthernet {

    companion object {
        internal const val ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGED"
        internal const val EXTRA_ETHERNET_STATE = "ethernet_state"

        internal const val ETHER_STATE_DISCONNECTED = 0
        internal const val ETHER_STATE_CONNECTING = 1
        internal const val ETHER_STATE_CONNECTED = 2
        internal const val ETHER_STATE_DISCONNECTING = 3
    }

    fun setEthernetStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    )

    /**
     * 开启以太网DHCP
     */
    fun setEthernetDhcpAddress(context: Context): Flow<@EthernetState Int>

    /**
     * 获取以太网网络信息
     */
    suspend fun getEthernetNetworkAddress(context: Context): NetworkAddress

}