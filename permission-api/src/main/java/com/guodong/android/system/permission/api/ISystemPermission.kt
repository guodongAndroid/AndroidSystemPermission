package com.guodong.android.system.permission.api

import android.app.ActivityThread
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import com.guodong.android.system.permission.api.annotation.Rotation
import com.guodong.android.system.permission.api.domain.NetworkAddress
import java.util.concurrent.TimeUnit

/**
 * Created by guodongAndroid on 2025/5/27
 */
@Keep
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
interface ISystemPermission {

    companion object {
        internal fun currentPackageName(): String? = ActivityThread.currentPackageName()
        internal fun currentProcessName(): String? = ActivityThread.currentProcessName()
        internal fun currentApplication(): Application? = ActivityThread.currentApplication()
    }

    /**
     * 设置上下文
     */
    fun setContext(context: Context)

    /**
     * 厂商，子类必须实现
     *
     * @see [Vendor]
     */
    @Vendor
    fun getVendor(): String

    /**
     * 获取版本
     */
    fun getVersion(): String

    /**
     * 是否启用以太网
     */
    suspend fun enableEthernet(enable: Boolean)

    /**
     * 以太网是否启用
     */
    suspend fun isEthernetEnabled(): Boolean

    /**
     * 设置以太网静态地址
     */
    suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean

    /**
     * 开启以太网DHCP
     */
    suspend fun setEthernetDhcpAddress(): Boolean

    /**
     * 获取以太网网络信息
     */
    suspend fun getEthernetNetworkAddress(): NetworkAddress

    /**
     * 获取以太网MAC地址
     */
    suspend fun getEthernetMacAddress(): String

    /**
     * 重启
     */
    fun reboot()

    /**
     * 关机
     */
    fun shutdown()

    /**
     * 恢复出厂设置
     */
    fun factoryReset()

    /**
     * 静默授予权限
     */
    fun grantRuntimePermission(packageName: String): Boolean

    /**
     * 获取系统桌面
     */
    fun getLauncher(): ComponentName?

    /**
     * 静默设置系统桌面
     */
    fun setLauncher(packageName: String): Boolean

    /**
     * 打开系统桌面
     */
    fun openSystemLauncher()

    /**
     * 打开系统设置
     */
    fun openSystemSettings()

    /**
     * 打开系统开发者选项设置
     */
    fun openSystemDevelopmentSettings()

    /**
     * 设置屏幕百分比亮度
     */
    fun setScreenBrightness(@IntRange(from = 1, to = 100) level: Int)

    /**
     * 获取屏幕百分比亮度
     */
    @IntRange(from = 1, to = 100)
    fun getScreenBrightness(): Int

    /**
     * 是否启用自动调节亮度
     */
    fun enableAutoBrightness(enable: Boolean): Boolean

    /**
     * 自动调节亮度是否启用
     */
    fun isAutoBrightnessEnabled(): Boolean

    /**
     * 是否启用深色主题
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun enableDarkUI(enable: Boolean): Boolean

    /**
     * 深色主题是否启用
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun isDarkUIEnabled(): Boolean

    /**
     * 是否启用屏幕永不关闭
     */
    fun enableScreenNeverOff(enable: Boolean)

    /**
     * 屏幕永不关闭是否启用
     */
    fun isScreenNeverOffEnabled(): Boolean

    /**
     * 亮屏
     */
    fun setScreenOn()

    /**
     * 息屏
     */
    fun setScreenOff()

    /**
     * 是否启用屏幕自动旋转
     */
    fun enableScreenAutoRotation(enable: Boolean)

    /**
     * 屏幕自动旋转是否启用
     */
    fun isScreenAutoRotationEnabled(): Boolean

    /**
     * 设置屏幕旋转，顺时针旋转
     */
    fun setScreenRotation(@Rotation rotation: Int)

    /**
     * 获取屏幕旋转
     */
    @Rotation
    fun getScreenRotation(): Int

    /**
     * 是否启用ADB
     */
    fun enableAdb(enable: Boolean)

    /**
     * ADB是否启用
     */
    fun isAdbEnabled(): Boolean

    /**
     * 设置ADB端口
     */
    fun setAdbPort(@IntRange(from = 5000, to = 65535) port: Int)

    /**
     * 获取ADB端口
     */
    @IntRange(from = 5000, to = 65535)
    fun getAdbPort(): Int

    /**
     * 是否启用状态栏和导航栏
     */
    fun enableSystemBar(enable: Boolean)

    /**
     * 状态栏和导航栏是否启用
     */
    fun isSystemBarEnabled(): Boolean

    /**
     * 设置系统时区
     */
    suspend fun setTimeZone(timeZone: String): Boolean

    /**
     * 设置系统日期
     */
    suspend fun setDate(year: Int, month: Int, day: Int): Boolean

    /**
     * 设置系统时间
     */
    suspend fun setTime(hour: Int, minute: Int, second: Int): Boolean

    /**
     * 是否启用24小时制
     */
    suspend fun enableTimeFormat24H(enable: Boolean): Boolean

    /**
     * 24小时制是否启用
     */
    suspend fun isTimeFormat24HEnabled(): Boolean

    /**
     * 清除应用程序用户数据，包含缓存数据
     */
    fun clearApplicationUserData(packageName: String, observer: IApplicationUserDataCleanObserver)

    /**
     * 静默安装
     */
    fun installPackage(apkFilePath: String, observer: IPackageInstallObserver)

    /**
     * 静默卸载
     */
    fun uninstallPackage(packageName: String, observer: IPackageDeleteObserver)

    /**
     * @see [android.app.ActivityManager.killBackgroundProcesses]
     */
    fun killBackgroundProcesses(packageName: String)

    /**
     * @see [android.app.ActivityManager.forceStopPackage]
     */
    fun forceStopPackage(packageName: String)

    /**
     * OTA升级
     */
    fun installOTAPackage(otaFilePath: String, observer: IOTAPackageInstallObserver)

    /**
     * 屏幕截图
     */
    suspend fun takeScreenShot(savePath: String): Boolean

    /**
     * 屏幕截图
     */
    fun takeScreenShot(): Bitmap?

    /**
     * 获取固件版本
     */
    suspend fun getFirmwareVersion(): String

    /**
     * 获取NTP服务器时间
     */
    suspend fun getNtpTime(
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Long

    /**
     * 将[packageName]添加进永久省电白名单
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun addToPermanentPowerSaveAllowList(packageName: String)

    /**
     * 将[packageName]从永久省电白名单中移除
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun removeToPermanentPowerSaveAllowList(packageName: String)

    /**
     * [packageName]是否是省电白名单应用程序
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isPowerSaveWhitelistApp(packageName: String): Boolean
}