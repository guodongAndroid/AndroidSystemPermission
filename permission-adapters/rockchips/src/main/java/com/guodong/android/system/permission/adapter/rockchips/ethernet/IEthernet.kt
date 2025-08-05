package com.guodong.android.system.permission.adapter.rockchips.ethernet

import android.annotation.SuppressLint
import android.content.Context
import android.net.IEthernetManager
import android.net.LinkAddress
import android.net.NetworkUtils
import android.net.ProxyInfo
import android.net.RouteInfo
import android.os.ServiceManager
import android.util.Log
import com.guodong.android.system.permission.android.util.isGateway
import com.guodong.android.system.permission.android.util.isIP
import com.guodong.android.system.permission.android.util.isNetMask
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Inet4Address
import java.net.InetAddress

/**
 * Created by john.wick on 2025/7/2
 */
internal interface IEthernet {

    companion object {
        internal const val TAG = "IEthernet"

        /**
         * @see [Context]
         */
        internal const val ETHERNET_SERVICE = "ethernet"

        internal const val ETH0_INTERFACE_NAME = "eth0"
    }

    fun getEthernetManager(): IEthernetManager {
        val binder = ServiceManager.getService(ETHERNET_SERVICE)
        return IEthernetManager.Stub.asInterface(binder)
    }

    fun setStaticAddress(
        context: Context,
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean

    /**
     * 开启以太网DHCP
     */
    fun setDhcpAddress(context: Context): Boolean

    /**
     * 获取以太网网络信息
     */
    suspend fun getNetworkAddress(context: Context): NetworkAddress

    /**
     * 获取以太网DHCP网络信息
     */
    suspend fun getDhcpNetworkAddress(context: Context): NetworkAddress

    /**
     * 获取以太网静态网络信息
     */
    suspend fun getStaticNetworkAddress(context: Context): NetworkAddress

    /**
     * 获取以太网MAC地址
     */
    suspend fun getMacAddress(): String = withContext(Dispatchers.IO) {
        File("/sys/class/net/eth0/address").readText()
    }
}

internal fun IEthernet.check(
    ipAddress: String,
    netmask: String,
    gateway: String,
    dns1: String,
) {
    if (ipAddress.isEmpty() || ipAddress.isBlank() || !ipAddress.isIP()) {
        throw IllegalArgumentException("Illegal ipAddress")
    }

    if (netmask.isEmpty() || netmask.isBlank() || !netmask.isNetMask()) {
        throw IllegalArgumentException("Illegal netmask")
    }

    if (gateway.isEmpty() || gateway.isBlank() || !gateway.isGateway(ipAddress, netmask)) {
        throw IllegalArgumentException("Illegal gateway")
    }

    if (dns1.isEmpty() || dns1.isBlank() || !dns1.isIP()) {
        throw IllegalArgumentException("Illegal dns1")
    }
}

/**
 * @receiver IpConfiguration
 */
@SuppressLint("PrivateApi")
internal fun Any.isStaticIpAssignment(): Boolean {
    val clazz = this.javaClass
    val getIpAssignment = clazz.getDeclaredMethod("getIpAssignment")
    getIpAssignment.isAccessible = true
    val ipAssignment = getIpAssignment.invoke(this) ?: return false

    @Suppress("UNCHECKED_CAST")
    val ipAssignmentClazz =
        Class.forName("android.net.IpConfiguration${"$"}IpAssignment") as Class<out Enum<*>>
    val staticIpAssignment = java.lang.Enum.valueOf(ipAssignmentClazz, "STATIC")
    return ipAssignment == staticIpAssignment
}

@SuppressLint("PrivateApi")
@Suppress("UNCHECKED_CAST")
internal fun IEthernet.buildIpConfiguration(isStatic: Boolean, staticIpConfiguration: Any?): Any? {
    return try {
        val ipAssignmentClazz =
            Class.forName("android.net.IpConfiguration${"$"}IpAssignment") as Class<out Enum<*>>
        val proxySettingsClazz =
            Class.forName("android.net.IpConfiguration${"$"}ProxySettings") as Class<out Enum<*>>
        val staticIpConfigurationClazz = Class.forName("android.net.StaticIpConfiguration")
        val clazz = Class.forName("android.net.IpConfiguration")

        // public IpConfiguration(IpAssignment ipAssignment, ProxySettings proxySettings, StaticIpConfiguration staticIpConfiguration, ProxyInfo httpProxy)
        val constructor = clazz.getDeclaredConstructor(
            ipAssignmentClazz,
            proxySettingsClazz,
            staticIpConfigurationClazz,
            ProxyInfo::class.java
        )
        constructor.isAccessible = true

        val ipAssignment = if (isStatic) {
            java.lang.Enum.valueOf(ipAssignmentClazz, "STATIC")
        } else {
            java.lang.Enum.valueOf(ipAssignmentClazz, "DHCP")
        }

        val proxySettings = java.lang.Enum.valueOf(proxySettingsClazz, "NONE")

        constructor.newInstance(ipAssignment, proxySettings, staticIpConfiguration, null)
    } catch (e: Exception) {
        Log.e(IEthernet.TAG, "buildIpConfiguration: ${e.message}", e)
        null
    }
}

@SuppressLint("PrivateApi")
internal fun IEthernet.buildStaticIpConfiguration(
    ipAddress: String,
    netmask: String,
    gateway: String,
    dns1: String,
    dns2: String,
): Any? {
    return try {

        val address = NetworkUtils.numericToInetAddress(ipAddress)
        val linkAddress = buildLinkAddress(address, netmask) ?: return null

        val clazz = Class.forName("android.net.StaticIpConfiguration")
        val constructor = clazz.getDeclaredConstructor()
        constructor.isAccessible = true
        val configuration = constructor.newInstance()

        val ipAddressField = clazz.getDeclaredField("ipAddress")
        ipAddressField.isAccessible = true
        ipAddressField.set(configuration, linkAddress)

        val gatewayField = clazz.getDeclaredField("gateway")
        gatewayField.isAccessible = true
        gatewayField.set(configuration, NetworkUtils.numericToInetAddress(gateway))

        val dnsServersField = clazz.getDeclaredField("dnsServers")
        dnsServersField.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val dnsServers = dnsServersField.get(configuration) as MutableList<InetAddress>
        dnsServers.add(NetworkUtils.numericToInetAddress(dns1))

        if (dns2.isIP()) {
            dnsServers.add(NetworkUtils.numericToInetAddress(dns2))
        }

        configuration
    } catch (e: Exception) {
        Log.e(IEthernet.TAG, "buildStaticIpConfiguration: ${e.message}", e)
        null
    }
}

internal fun IEthernet.buildLinkAddress(ipAddress: InetAddress, netmask: String): LinkAddress? {
    return try {
        val clazz = LinkAddress::class.java

        // public LinkAddress(InetAddress address, int prefixLength)
        val constructor =
            clazz.getDeclaredConstructor(InetAddress::class.java, Int::class.java)
        constructor.isAccessible = true

        val prefixLength =
            NetworkUtils.netmaskToPrefixLength(NetworkUtils.numericToInetAddress(netmask) as Inet4Address)

        return constructor.newInstance(ipAddress, prefixLength)
    } catch (e: Exception) {
        Log.e(IEthernet.TAG, "buildLinkAddress: ${e.message}", e)
        null
    }
}

@Suppress("FunctionName")
internal fun RouteInfo._hasGateway(): Boolean {
    val clazz = this.javaClass
    return clazz.getDeclaredMethod("hasGateway").apply {
        isAccessible = true
    }.invoke(this) as Boolean
}

@SuppressLint("PrivateApi")
internal fun RouteInfo.isIPv4Default(): Boolean {
    val clazz = this.javaClass
    return clazz.getDeclaredMethod("isIPv4Default").apply {
        isAccessible = true
    }.invoke(this) as Boolean
}