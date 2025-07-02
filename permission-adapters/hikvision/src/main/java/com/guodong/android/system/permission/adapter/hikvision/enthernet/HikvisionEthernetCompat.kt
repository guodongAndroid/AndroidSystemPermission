package com.guodong.android.system.permission.adapter.hikvision.enthernet

import android.content.Context
import android.os.Build
import com.guodong.android.system.permission.domain.NetworkAddress
import com.guodong.android.system.permission.ethernet.IEthernet
import kotlinx.coroutines.flow.Flow

/**
 * Created by john.wick on 2025/7/2
 */
internal object HikvisionEthernetCompat : IEthernet {

    private val ethernet = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            EthernetApi29
        }

        else -> {
            EthernetApi24
        }
    }

    override fun setEthernetStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ) {
        ethernet.setEthernetStaticAddress(context, ipAddress, netmask, gateway, dns1, dns2)
    }

    override fun setEthernetDhcpAddress(context: Context): Flow<Int> {
        return ethernet.setEthernetDhcpAddress(context)
    }

    override suspend fun getEthernetNetworkAddress(context: Context): NetworkAddress {
        return ethernet.getEthernetNetworkAddress(context)
    }
}