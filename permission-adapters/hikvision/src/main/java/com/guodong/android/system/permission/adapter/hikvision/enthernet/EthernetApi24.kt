package com.guodong.android.system.permission.adapter.hikvision.enthernet

import android.content.Context
import com.guodong.android.system.permission.domain.NetworkAddress
import com.guodong.android.system.permission.ethernet.IEthernet
import kotlinx.coroutines.flow.Flow

/**
 * Created by john.wick on 2025/7/2
 */
internal object EthernetApi24 : IEthernet {

    private const val TAG = "EthernetApi24"

    override fun setEthernetStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ) {
    }

    override fun setEthernetDhcpAddress(context: Context): Flow<Int> {
        TODO()
    }

    override suspend fun getEthernetNetworkAddress(context: Context): NetworkAddress {
        TODO()
    }
}