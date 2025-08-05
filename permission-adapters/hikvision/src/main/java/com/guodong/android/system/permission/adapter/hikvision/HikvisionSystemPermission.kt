package com.guodong.android.system.permission.adapter.hikvision

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemProperties
import android.provider.Settings
import android.util.Log
import androidx.annotation.Keep
import com.guodong.android.system.permission.adapter.rockchips.RockChipsSystemPermission
import com.guodong.android.system.permission.domain.NetworkAddress
import com.hik.vis.module_base.IHikCallback
import com.hik.vis.module_base.beans.ResponseStatus
import com.hik.vis.module_base.constant.FactoryResetMode
import com.hik.vis.module_sdk.IHikManager
import com.hik.vis.module_sdk.constant.IHikConfig
import com.hik.vis.module_system.beans.response.DeviceInfo
import com.hik.vis.module_system.beans.response.IpAddress
import com.hik.vis.module_system.constant.AddressType
import com.hik.vis.module_system.constant.IpVersion
import com.hik.vis.module_system.interfaces.ISystemManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by john.wick on 2025/7/2
 */
@Keep
class HikvisionSystemPermission : RockChipsSystemPermission() {

    @Suppress("SpellCheckingInspection")
    companion object {
        private const val TAG = "HikvisionSystemPermission"

        private const val KEY_LOG_LEVEL = "persist.log.tag"

        private const val ACTION_SHOW_NAVI_BAR = "android.systemui.SHOW_NAVI_BAR"
        private const val ACTION_HIDE_NAVI_BAR = "android.systemui.HIDE_NAVI_BAR"

        private const val ACTION_SHOW_STATUS_BAR = "android.systemui.SHOW_STATUS_BAR"
        private const val ACTION_HIDE_STATUS_BAR = "android.systemui.HIDE_STATUS_BAR"

        private const val EMPTY_CHAR_SEQUENCE = ""
    }

    private lateinit var system: ISystemManager

    override fun setContext(context: Context) {
        super.setContext(context)
        SystemProperties.set(KEY_LOG_LEVEL, LogLevel.DEBUG)

        val config = IHikConfig.createConfig(context.applicationContext)
            .setClientName(TAG)
        IHikManager.init(config)
        system = IHikManager.getSystemManager()
    }

    override suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ): Boolean = suspendCoroutine { cont ->
        system.setNetworkAddress(
            ipAddress,
            netmask,
            gateway,
            dns1,
            dns2,
            object : IHikCallback<ResponseStatus>() {
                override fun onSuccess(data: ResponseStatus?) {
                    Log.d(TAG, "setEthernetStaticAddress onSuccess: $data")
                    cont.resume(data?.isOK ?: false)
                }

                override fun onFailure(responseStatus: ResponseStatus?) {
                    Log.e(TAG, "setEthernetStaticAddress onFailure: $responseStatus")
                    cont.resume(false)
                }
            })
    }

    override suspend fun setEthernetDhcpAddress(): Boolean = suspendCoroutine { cont ->
        system.setDhcpAddress(IpVersion.v4, object : IHikCallback<ResponseStatus>() {
            override fun onSuccess(data: ResponseStatus?) {
                Log.d(TAG, "setEthernetDhcpAddress onSuccess: $data")
                cont.resume(data?.isOK ?: false)
            }

            override fun onFailure(responseStatus: ResponseStatus?) {
                Log.e(TAG, "setEthernetDhcpAddress onFailure: $responseStatus")
                cont.resume(false)
            }
        })
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress = suspendCoroutine { cont ->
        system.getNetworkAddress(object : IHikCallback<IpAddress>() {
            override fun onSuccess(data: IpAddress) {
                Log.d(TAG, "getEthernetNetworkAddress onSuccess: $data")
                val ipAssignment = when (data.addressingType) {
                    AddressType.STATIC.value -> NetworkAddress.IpAssignment.STATIC
                    AddressType.DYNAMIC.value -> NetworkAddress.IpAssignment.DHCP
                    else -> NetworkAddress.IpAssignment.UNASSIGNED
                }

                val address = NetworkAddress(
                    ipAssignment,
                    data.ipAddress,
                    data.subnetMask,
                    data.defaultGateway.ipAddress,
                    data.primaryDns.ipAddress,
                    data.secondaryDns.ipAddress
                )

                cont.resume(address)
            }

            override fun onFailure(responseStatus: ResponseStatus) {
                Log.e(TAG, "getEthernetNetworkAddress onFailure: $responseStatus")
                cont.resume(NetworkAddress.UNASSIGNED)
            }
        })
    }

    override suspend fun getEthernetMacAddress(): String = suspendCoroutine { cont ->
        system.getDeviceInfo(object : IHikCallback<DeviceInfo>() {
            override fun onSuccess(data: DeviceInfo?) {
                Log.d(TAG, "getEthernetMacAddress onSuccess: $data")
                cont.resume(data?.macAddress ?: EMPTY_CHAR_SEQUENCE)
            }

            override fun onFailure(responseStatus: ResponseStatus?) {
                Log.e(TAG, "getEthernetMacAddress onFailure: $responseStatus")
                cont.resume(EMPTY_CHAR_SEQUENCE)
            }
        })
    }

    override fun reboot(): Boolean {
        system.rebootDevice()
        return true
    }

    override fun factoryReset() {
        system.factoryReset(FactoryResetMode.FULL)
    }

    override fun setScreenBright(level: Int) {
        val levelTemp = level.coerceIn(1, 100)
        if (levelTemp != getScreenBright()) {
            system.setScreenBright(levelTemp, null)
        }
    }

    override fun getScreenBright(): Int {
        return system.screenBright
    }

    override fun setScreenOn() {
        system.setScreenOn()
    }

    override fun setScreenOff() {
        system.setScreenOff()
    }

    override fun enableAdb(enable: Boolean) {
        system.setAdb(enable, null)
        super.enableAdb(enable)
    }

    override fun isAdbEnabled(): Boolean {
        return system.adbStatus
    }

    override fun hideSystemBar(): Boolean {
        system.controlNavigationBar(false)

        Settings.System.putInt(context.contentResolver, ACTION_HIDE_STATUS_BAR, 0)
        Settings.System.putInt(context.contentResolver, ACTION_HIDE_NAVI_BAR, 0)

        context.sendBroadcast(Intent(ACTION_HIDE_STATUS_BAR))
        context.sendBroadcast(Intent(ACTION_HIDE_NAVI_BAR))
        return true
    }

    override fun showSystemBar(): Boolean {
        system.controlNavigationBar(true)

        Settings.System.putInt(context.contentResolver, ACTION_SHOW_STATUS_BAR, 0)
        Settings.System.putInt(context.contentResolver, ACTION_SHOW_NAVI_BAR, 0)

        context.sendBroadcast(Intent(ACTION_SHOW_STATUS_BAR))
        context.sendBroadcast(Intent(ACTION_SHOW_NAVI_BAR))

        return true
    }

    override suspend fun setDate(year: Int, month: Int, day: Int): Boolean =
        suspendCoroutine { cont ->
            system.setDeviceDate(year, month + 1, day, object : IHikCallback<String>() {
                override fun onSuccess(data: String?) {
                    Log.d(TAG, "setDate onSuccess: $data")
                    cont.resume(data != null)
                }

                override fun onFailure(responseStatus: ResponseStatus?) {
                    Log.e(TAG, "setDate onFailure: $responseStatus")
                    cont.resume(false)
                }
            })
        }

    override suspend fun setTime(hour: Int, minute: Int, second: Int): Boolean =
        suspendCoroutine { cont ->
            system.setDeviceTime(hour, minute, second, object : IHikCallback<String>() {
                override fun onSuccess(data: String?) {
                    Log.d(TAG, "setTime onSuccess: $data")
                    cont.resume(data != null)
                }

                override fun onFailure(responseStatus: ResponseStatus?) {
                    Log.e(TAG, "setTime onFailure: $responseStatus")
                    cont.resume(false)
                }
            })
        }

    override suspend fun getFirmwareVersion(): String = suspendCoroutine { cont ->
        system.getDeviceInfo(object : IHikCallback<DeviceInfo>() {
            override fun onSuccess(data: DeviceInfo?) {
                Log.d(TAG, "getFirmwareVersion onSuccess: $data")
                if (data == null) {
                    cont.resume(Build.DISPLAY)
                } else {
                    cont.resume("${data.firmwareVersion}_${data.firmwareReleasedDate}")
                }
            }

            override fun onFailure(responseStatus: ResponseStatus?) {
                Log.e(TAG, "getFirmwareVersion onFailure: $responseStatus")
                cont.resume(EMPTY_CHAR_SEQUENCE)
            }
        })
    }
}