package com.guodong.android.system.permission.api.util

import java.net.Inet4Address

/**
 * Created by guodongAndroid on 2025/7/3
 */
val REGEX_IP = """((25[0-5]|2[0-4]\d|[10]?\d?\d)\.){3}(25[0-5]|2[0-4]\d|[10]?\d?\d)""".toRegex()
val REGEX_NET_MASK = """(255\.(255\.(255\.(255|254|252|248|240|224|192|128|0)|0)|0)|0)""".toRegex()

/**
 * 将 IPv4 字符串转换为整型
 */
fun String.ip2Long(): Long {
    return Inet4Address.getByName(this).address.fold(0L) { acc, byte ->
        (acc shl 8) or (byte.toInt() and 0xFF).toLong()
    }
}

/**
 * 根据IP和子网掩码计算网络地址
 */
fun calculateNetworkAddress(ip: String, subnetMask: String): Long {
    val ipLong = ip.ip2Long()
    val maskLong = subnetMask.ip2Long()
    return ipLong and maskLong
}

/**
 * 根据子网掩码计算广播地址
 */
fun calculateBroadcastAddress(networkAddress: Long, subnetMask: String): Long {
    val maskLong = subnetMask.ip2Long()
    return networkAddress or maskLong.inv() and 0xFFFFFFFF
}

fun CharSequence.isIP(): Boolean = REGEX_IP.matches(this)

fun CharSequence.isNetMask(): Boolean = REGEX_NET_MASK.matches(this)

fun CharSequence.isGateway(ip: CharSequence, netmask: CharSequence): Boolean {
    if (!isIP() || !ip.isIP() || !netmask.isIP()) {
        return false
    }

    val networkAddress = calculateNetworkAddress(ip.toString(), netmask.toString())
    val broadcastAddress = calculateBroadcastAddress(networkAddress, netmask.toString())
    val gatewayLong = this.toString().ip2Long()

    return gatewayLong in (networkAddress + 1)..<broadcastAddress
}