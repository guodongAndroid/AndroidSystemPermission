package com.guodong.android.system.permission.adapter.rockchips.ethernet

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.IEthernetManager
import android.net.LinkAddress
import android.net.NetworkCapabilities
import android.net.NetworkUtils
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.guodong.android.system.permission.domain.NetworkAddress
import java.net.Inet4Address
import java.net.InetAddress

/**
 * Created by john.wick on 2025/7/3
 */
@RequiresApi(Build.VERSION_CODES.P)
internal object EthernetApi28 : IEthernet {

    private const val TAG = "EthernetApi28"

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
            val staticIpConfiguration =
                buildStaticIpConfiguration(ipAddress, netmask, gateway, dns1, dns2) ?: return false
            val ipConfiguration = buildIpConfiguration(true, staticIpConfiguration) ?: return false

            val manager = getEthernetManager()
            manager.setConfiguration(ipConfiguration)
            true
        } catch (e: Exception) {
            Log.e(TAG, "setEthernetStaticAddress: ${e.message}", e)
            false
        }
    }

    override fun setDhcpAddress(context: Context): Boolean {
        return try {
            val ipConfiguration = buildIpConfiguration(false, null) ?: return false
            val manager = getEthernetManager()
            manager.setConfiguration(ipConfiguration)
            true
        } catch (e: Exception) {
            Log.e(TAG, "setEthernetDhcpAddress: ${e.message}", e)
            false
        }
    }

    override suspend fun getNetworkAddress(context: Context): NetworkAddress {
        return try {
            val manager = getEthernetManager()
            val configuration = manager.getConfiguration() ?: return NetworkAddress.UNASSIGNED

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
        val configuration = manager.getConfiguration() ?: return NetworkAddress.UNASSIGNED

        if (!configuration.isStaticIpAssignment()) {
            return NetworkAddress.UNASSIGNED
        }

        val clazz = this.javaClass
        val getStaticIpConfiguration = clazz.getDeclaredMethod("getStaticIpConfiguration")
        getStaticIpConfiguration.isAccessible = true
        val staticIpConfiguration =
            getStaticIpConfiguration.invoke(this) ?: return NetworkAddress.UNASSIGNED

        val staticIpConfigurationClazz = staticIpConfiguration.javaClass
        val ipAddressField = staticIpConfigurationClazz.getDeclaredField("ipAddress")
        ipAddressField.isAccessible = true
        val linkAddress =
            ipAddressField.get(staticIpConfiguration) as? LinkAddress
                ?: return NetworkAddress.UNASSIGNED
        val ipAddress = linkAddress.address.hostAddress ?: return NetworkAddress.UNASSIGNED
        val netmask =
            NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(linkAddress.prefixLength)).hostAddress
                ?: return NetworkAddress.UNASSIGNED

        val gatewayField = staticIpConfigurationClazz.getDeclaredField("gateway")
        gatewayField.isAccessible = true
        val gatewayAddress =
            gatewayField.get(staticIpConfiguration) as? InetAddress
                ?: return NetworkAddress.UNASSIGNED
        val gateway = gatewayAddress.hostAddress ?: return NetworkAddress.UNASSIGNED

        val dnsServersField = staticIpConfigurationClazz.getDeclaredField("dnsServers")
        dnsServersField.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val dnsServers = dnsServersField.get(staticIpConfiguration) as MutableList<InetAddress>
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

    /**
     * @param config IpConfiguration
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun IEthernetManager.setConfiguration(config: Any /* IpConfiguration */) {
        val ipConfigurationClazz = Class.forName("android.net.IpConfiguration")
        val clazz = this.javaClass
        val setConfiguration =
            clazz.getDeclaredMethod("setConfiguration", String::class.java, ipConfigurationClazz)
        setConfiguration.isAccessible = true
        setConfiguration.invoke(this, IEthernet.ETH0_INTERFACE_NAME, config)
    }

    /**
     * @return IpConfiguration
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun IEthernetManager.getConfiguration(): Any? {
        return try {
            val clazz = this.javaClass
            val getConfiguration = clazz.getDeclaredMethod("getConfiguration", String::class.java)
            getConfiguration.isAccessible = true
            getConfiguration.invoke(this, IEthernet.ETH0_INTERFACE_NAME)
        } catch (e: Exception) {
            Log.e(TAG, "getConfiguration: ${e.message}", e)
            null
        }
    }
}