package com.guodong.android.system.permission.ethernet

import android.content.Context
import androidx.annotation.Keep
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.flow.Flow

/**
 * Created by john.wick on 2025/7/2
 */
@Keep
object EthernetCompat : IEthernet {

    private lateinit var delegate: IEthernet

    fun setDelegate(delegate: IEthernet) {
        this.delegate = delegate
    }

    override fun setEthernetStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ) {
        delegate.setEthernetStaticAddress(context, ipAddress, netmask, gateway, dns1, dns2)
    }

    override fun setEthernetDhcpAddress(context: Context): Flow<Int> {
        return delegate.setEthernetDhcpAddress(context)
    }

    override suspend fun getEthernetNetworkAddress(context: Context): NetworkAddress {
        return delegate.getEthernetNetworkAddress(context)
    }
}