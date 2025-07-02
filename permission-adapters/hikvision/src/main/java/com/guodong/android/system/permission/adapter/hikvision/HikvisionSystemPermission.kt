package com.guodong.android.system.permission.adapter.hikvision

import android.content.Context
import com.guodong.android.system.permission.AndroidStandardSystemPermission
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.flow.Flow

/**
 * Created by john.wick on 2025/7/2
 */
class HikvisionSystemPermission : AndroidStandardSystemPermission() {

    companion object {
        private const val TAG = "HikvisionSystemPermission"
    }

    override fun setContext(context: Context) {
        super.setContext(context)

    }

    override fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ) {
        super.setEthernetStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
    }

    override fun setEthernetDhcpAddress(): Flow<Int> {
        return super.setEthernetDhcpAddress()
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return super.getEthernetNetworkAddress()
    }
}