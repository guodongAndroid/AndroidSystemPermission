package com.guodong.android.system.permission

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Keep
import com.guodong.android.system.permission.annotation.Orientation
import com.guodong.android.system.permission.domain.NetworkAddress
import java.util.concurrent.TimeUnit

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
object SystemPermissionCompat : ISystemPermission {

    private lateinit var delegate: ISystemPermission

    fun setDelegate(delegate: ISystemPermission) {
        this.delegate = delegate
    }

    override fun setContext(context: Context) {
        delegate.setContext(context)
    }

    override fun getVersion(): String {
        return delegate.getVersion()
    }

    override fun setEthernetStaticAddress(
        ipAddress: String,
        subnetMask: String,
        gateway: String,
        primaryDNS: String,
        secondaryDNS: String
    ) {
        delegate.setEthernetStaticAddress(ipAddress, subnetMask, gateway, primaryDNS, secondaryDNS)
    }

    override fun setEthernetDhcpAddress() {
        delegate.setEthernetDhcpAddress()
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return delegate.getEthernetNetworkAddress()
    }

    override fun reboot() {
        delegate.reboot()
    }

    override fun factoryReset() {
        delegate.factoryReset()
    }

    override fun grantRuntimePermission(packageName: String): Boolean {
        return delegate.grantRuntimePermission(packageName)
    }

    override fun setLauncher(packageName: String): Boolean {
        return delegate.setLauncher(packageName)
    }

    override fun openSystemLauncher() {
        delegate.openSystemLauncher()
    }

    override fun openSystemSettings() {
        delegate.openSystemSettings()
    }

    override fun openSystemDevelopmentSettings() {
        delegate.openSystemDevelopmentSettings()
    }

    override fun setScreenBright(level: Int) {
        delegate.setScreenBright(level)
    }

    override fun getScreenBright(): Int {
        return delegate.getScreenBright()
    }

    override fun enableAutoBrightness(enable: Boolean): Boolean {
        return delegate.enableAutoBrightness(enable)
    }

    override fun isAutoBrightnessEnabled(): Boolean {
        return delegate.isAutoBrightnessEnabled()
    }

    override fun enableScreenNeverOff(enable: Boolean) {
        delegate.enableScreenNeverOff(enable)
    }

    override fun isScreenNeverOffEnabled(): Boolean {
        return delegate.isScreenNeverOffEnabled()
    }

    override fun setScreenOn() {
        delegate.setScreenOn()
    }

    override fun setScreenOff() {
        delegate.setScreenOff()
    }

    override fun enableAdb(enable: Boolean) {
        delegate.enableAdb(enable)
    }

    override fun isAdbEnabled(): Boolean {
        return delegate.isAdbEnabled()
    }

    override fun hideSystemBar() {
        delegate.hideSystemBar()
    }

    override fun showSystemBar() {
        delegate.showSystemBar()
    }

    override fun setDate(year: Int, month: Int, day: Int) {
        delegate.setDate(year, month, day)
    }

    override fun setTime(hour: Int, minute: Int, second: Int) {
        delegate.setTime(hour, minute, second)
    }

    override fun setOrientation(@Orientation angle: Int) {
        delegate.setOrientation(angle)
    }

    override fun clearApplicationUserData(
        packageName: String,
        observer: IApplicationUserDataCleanObserver
    ) {
        delegate.clearApplicationUserData(packageName, observer)
    }

    override fun installPackage(apkFilePath: String, observer: IPackageInstallObserver) {
        delegate.installPackage(apkFilePath, observer)
    }

    override fun uninstallPackage(packageName: String, observer: IPackageDeleteObserver) {
        delegate.uninstallPackage(packageName, observer)
    }

    override fun installOTAPackage(otaFilePath: String, observer: IOTAPackageInstallObserver) {
        delegate.installOTAPackage(otaFilePath, observer)
    }

    override suspend fun takeScreenShot(savePath: String): Boolean {
        return delegate.takeScreenShot(savePath)
    }

    override fun takeScreenShot(): Bitmap? {
        return delegate.takeScreenShot()
    }

    override suspend fun getNtpTime(
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        return delegate.getNtpTime(ntpServer, ntpPort, timeout, timeUnit)
    }
}