package com.guodong.android.system.permission

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.IntRange
import androidx.annotation.Keep
import com.guodong.android.system.permission.annotation.EthernetState
import com.guodong.android.system.permission.annotation.Orientation
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
interface ISystemPermission {

    companion object {
        private const val TAG = "ISystemPermission"
    }

    /**
     * 设置上下文
     */
    fun setContext(context: Context)

    /**
     * 获取版本
     */
    fun getVersion(): String

    /**
     * 设置以太网静态地址
     */
    fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    )

    /**
     * 开启以太网DHCP
     */
    fun setEthernetDhcpAddress(): Flow<@EthernetState Int>

    /**
     * 获取以太网网络信息
     */
    suspend fun getEthernetNetworkAddress(): NetworkAddress

    /**
     * 重启设备
     */
    fun reboot()

    /**
     * 恢复出厂设置
     */
    fun factoryReset()

    /**
     * 静默授予权限
     */
    fun grantRuntimePermission(packageName: String): Boolean

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
    fun setScreenBright(@IntRange(from = 1, to = 100) level: Int)

    /**
     * 获取屏幕百分比亮度
     */
    @IntRange(from = 1, to = 100)
    fun getScreenBright(): Int

    /**
     * 是否启用自动调节亮度
     */
    fun enableAutoBrightness(enable: Boolean): Boolean

    /**
     * 自动调节亮度是否启用
     */
    fun isAutoBrightnessEnabled(): Boolean

    /**
     * 是否启动永不关闭屏幕
     */
    fun enableScreenNeverOff(enable: Boolean)

    /**
     * 永不关闭屏幕是否启用
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
     * 是否启用ADB
     */
    fun enableAdb(enable: Boolean)

    /**
     * ADB是否启用
     */
    fun isAdbEnabled(): Boolean

    /**
     * 隐藏状态栏和导航栏
     */
    fun hideSystemBar()

    /**
     * 显示状态栏和导航栏
     */
    fun showSystemBar()

    /**
     * 设置系统日期
     */
    fun setDate(year: Int, month: Int, day: Int)

    /**
     * 设置系统时间
     */
    fun setTime(hour: Int, minute: Int, second: Int)

    /**
     * 设置屏幕旋转方向，顺时针旋转
     */
    fun setOrientation(@Orientation angle: Int)

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
     * 获取NTP服务器时间
     */
    suspend fun getNtpTime(
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit,
    ): Long
}