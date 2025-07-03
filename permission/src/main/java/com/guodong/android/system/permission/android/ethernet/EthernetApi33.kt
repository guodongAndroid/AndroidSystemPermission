package com.guodong.android.system.permission.android.ethernet

import android.content.Context
import android.net.ConnectivityManager
import android.net.IpConfiguration
import android.net.NetworkCapabilities
import android.net.NetworkUtils
import android.net.StaticIpConfiguration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.guodong.android.system.permission.android.util.isIP
import com.guodong.android.system.permission.domain.NetworkAddress
import java.net.Inet4Address

/**
 * Created by john.wick on 2025/7/3
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal object EthernetApi33 : IEthernet {

    private const val TAG = "EthernetApi33"

    override fun setStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ): Boolean {
        check(ipAddress, netmask, gateway, dns1)
        return try {
            val linkAddress =
                buildLinkAddress(NetworkUtils.numericToInetAddress(ipAddress), netmask)
                    ?: return false

            val dnsServers = mutableListOf(NetworkUtils.numericToInetAddress(dns1)).apply {
                if (dns2.isIP()) {
                    add(NetworkUtils.numericToInetAddress(dns2))
                }
            }.toList()

            val staticIpConfiguration = StaticIpConfiguration.Builder()
                .setIpAddress(linkAddress)
                .setGateway(NetworkUtils.numericToInetAddress(gateway))
                .setDnsServers(dnsServers)
                .build()

            val ipConfiguration = IpConfiguration.Builder()
                .setStaticIpConfiguration(staticIpConfiguration)
                .setHttpProxy(null)
                .build()

            val manager = getEthernetManager()
            manager.setConfiguration(IEthernet.ETH0_INTERFACE_NAME, ipConfiguration)
            true
        } catch (e: Exception) {
            Log.e(TAG, "setEthernetStaticAddress: ${e.message}", e)
            false
        }
    }

    override fun setDhcpAddress(context: Context): Boolean {
        return try {
            val ipConfiguration = IpConfiguration.Builder()
                .setStaticIpConfiguration(null)
                .setHttpProxy(null)
                .build()
            val manager = getEthernetManager()
            manager.setConfiguration(IEthernet.ETH0_INTERFACE_NAME, ipConfiguration)
            true
        } catch (e: Exception) {
            Log.e(TAG, "setEthernetDhcpAddress: ${e.message}", e)
            false
        }
    }

    override suspend fun getNetworkAddress(context: Context): NetworkAddress {
        return try {
            val manager = getEthernetManager()
            val configuration =
                manager.getConfiguration(IEthernet.ETH0_INTERFACE_NAME)
                    ?: return NetworkAddress.UNASSIGNED

            if (configuration.isStaticIpAssignment()) {
                getStaticNetworkAddress(context)
            } else {
                getDhcpNetworkAddress(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getEthernetNetworkAddress: ${e.message}", e)
            NetworkAddress.UNASSIGNED
        }
    }

    override suspend fun getDhcpNetworkAddress(context: Context): NetworkAddress {
        val manager =
            context.getSystemService<ConnectivityManager>() ?: return NetworkAddress.UNASSIGNED

        @Suppress("DEPRECATION")
        val linkProperties = (manager.allNetworks.filterNotNull().filter {
            val caps = manager.getNetworkCapabilities(it)
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        }.mapNotNull {
            manager.getLinkProperties(it)
        }.firstOrNull {
            IEthernet.ETH0_INTERFACE_NAME == it.interfaceName
        } ?: return NetworkAddress.UNASSIGNED)

        val linkAddress = linkProperties.linkAddresses.firstOrNull()
            ?: return NetworkAddress.UNASSIGNED
        val inet4Address = linkAddress.address as? Inet4Address ?: return NetworkAddress.UNASSIGNED
        val ipAddress = inet4Address.hostAddress ?: return NetworkAddress.UNASSIGNED
        val netmask =
            NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(linkAddress.prefixLength)).hostAddress
                ?: return NetworkAddress.UNASSIGNED
        val gateway = linkProperties.routes.filter {
            it._hasGateway() && it.isIPv4Default()
        }.firstNotNullOfOrNull { it.gateway?.hostAddress } ?: return NetworkAddress.UNASSIGNED

        val dnsServers = linkProperties.dnsServers
        val dns1 = dnsServers.firstOrNull()?.hostAddress ?: return NetworkAddress.UNASSIGNED
        val dns2 = dnsServers.getOrNull(1)?.hostAddress.orEmpty()

        return NetworkAddress(
            NetworkAddress.IpAssignment.DHCP,
            ipAddress,
            netmask,
            gateway,
            dns1,
            dns2,
        )
    }

    override suspend fun getStaticNetworkAddress(context: Context): NetworkAddress {
        val manager = getEthernetManager()
        val configuration = manager.getConfiguration(IEthernet.ETH0_INTERFACE_NAME)
            ?: return NetworkAddress.UNASSIGNED

        if (!configuration.isStaticIpAssignment()) {
            return NetworkAddress.UNASSIGNED
        }

        val staticIpConfiguration =
            configuration.staticIpConfiguration ?: return NetworkAddress.UNASSIGNED

        val linkAddress = staticIpConfiguration.ipAddress
        val ipAddress = linkAddress.address.hostAddress ?: return NetworkAddress.UNASSIGNED
        val netmask =
            NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(linkAddress.prefixLength)).hostAddress
                ?: return NetworkAddress.UNASSIGNED

        val gateway = staticIpConfiguration.gateway?.hostAddress ?: return NetworkAddress.UNASSIGNED
        val dnsServers = staticIpConfiguration.dnsServers
        val dns1 = dnsServers.firstOrNull()?.hostAddress ?: return NetworkAddress.UNASSIGNED
        val dns2 = dnsServers.getOrNull(1)?.hostAddress.orEmpty()

        return NetworkAddress(
            NetworkAddress.IpAssignment.STATIC,
            ipAddress,
            netmask,
            gateway,
            dns1,
            dns2,
        )
    }
}