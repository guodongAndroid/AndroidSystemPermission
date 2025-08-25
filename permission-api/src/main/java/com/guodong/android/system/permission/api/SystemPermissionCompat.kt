package com.guodong.android.system.permission.api

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.guodong.android.system.permission.api.annotation.Rotation
import com.guodong.android.system.permission.api.domain.NetworkAddress
import java.util.concurrent.TimeUnit

/**
 * Created by guodongAndroid on 2025/5/27
 */
@Keep
object SystemPermissionCompat : ISystemPermission {

    private lateinit var delegate: ISystemPermission

    @JvmStatic
    fun currentPackageName(): String? = ISystemPermission.currentPackageName()

    @JvmStatic
    fun currentProcessName(): String? = ISystemPermission.currentProcessName()

    @JvmStatic
    fun currentApplication(): Application? = ISystemPermission.currentApplication()

    fun setDelegate(delegate: ISystemPermission) {
        SystemPermissionCompat.delegate = delegate
    }

    override fun setContext(context: Context) {
        delegate.setContext(context)
    }

    override fun getVendor(): String {
        return delegate.getVendor()
    }

    override fun getVersion(): String {
        return delegate.getVersion()
    }

    override suspend fun enableEthernet(enable: Boolean) {
        delegate.enableEthernet(enable)
    }

    override suspend fun isEthernetEnabled(): Boolean {
        return delegate.isEthernetEnabled()
    }

    override suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String
    ): Boolean {
        return delegate.setEthernetStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
    }

    override suspend fun setEthernetDhcpAddress(): Boolean {
        return delegate.setEthernetDhcpAddress()
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return delegate.getEthernetNetworkAddress()
    }

    override suspend fun getEthernetMacAddress(): String {
        return delegate.getEthernetMacAddress()
    }

    override fun reboot() {
        delegate.reboot()
    }

    override fun shutdown() {
        delegate.shutdown()
    }

    override fun factoryReset() {
        delegate.factoryReset()
    }

    override fun grantRuntimePermission(packageName: String): Boolean {
        return delegate.grantRuntimePermission(packageName)
    }

    override fun getLauncher(): ComponentName? {
        return delegate.getLauncher()
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

    override fun setScreenBrightness(level: Int) {
        delegate.setScreenBrightness(level)
    }

    override fun getScreenBrightness(): Int {
        return delegate.getScreenBrightness()
    }

    override fun enableAutoBrightness(enable: Boolean): Boolean {
        return delegate.enableAutoBrightness(enable)
    }

    override fun isAutoBrightnessEnabled(): Boolean {
        return delegate.isAutoBrightnessEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun enableDarkUI(enable: Boolean): Boolean {
        return delegate.enableDarkUI(enable)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun isDarkUIEnabled(): Boolean {
        return delegate.isDarkUIEnabled()
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

    override fun enableScreenAutoRotation(enable: Boolean) {
        delegate.enableScreenAutoRotation(enable)
    }

    override fun isScreenAutoRotationEnabled(): Boolean {
        return delegate.isScreenAutoRotationEnabled()
    }

    override fun setScreenRotation(@Rotation rotation: Int) {
        delegate.setScreenRotation(rotation)
    }

    @Rotation
    override fun getScreenRotation(): Int {
        return delegate.getScreenRotation()
    }

    override fun enableAdb(enable: Boolean) {
        delegate.enableAdb(enable)
    }

    override fun isAdbEnabled(): Boolean {
        return delegate.isAdbEnabled()
    }

    override fun setAdbPort(@IntRange(from = 5000, to = 65535) port: Int) {
        delegate.setAdbPort(port)
    }

    @IntRange(from = 5000, to = 65535)
    override fun getAdbPort(): Int {
        return delegate.getAdbPort()
    }

    override fun enableSystemBar(enable: Boolean) {
        delegate.enableSystemBar(enable)
    }

    override fun isSystemBarEnabled(): Boolean {
        return delegate.isSystemBarEnabled()
    }

    override suspend fun setTimeZone(timeZone: String): Boolean {
        return delegate.setTimeZone(timeZone)
    }

    override suspend fun setDate(year: Int, month: Int, day: Int): Boolean {
        return delegate.setDate(year, month, day)
    }

    override suspend fun setTime(hour: Int, minute: Int, second: Int): Boolean {
        return delegate.setTime(hour, minute, second)
    }

    override suspend fun enableTimeFormat24H(enable: Boolean): Boolean {
        return delegate.enableTimeFormat24H(enable)
    }

    override suspend fun isTimeFormat24HEnabled(): Boolean {
        return delegate.isTimeFormat24HEnabled()
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

    override fun killBackgroundProcesses(packageName: String) {
        delegate.killBackgroundProcesses(packageName)
    }

    override fun forceStopPackage(packageName: String) {
        delegate.forceStopPackage(packageName)
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

    override suspend fun getFirmwareVersion(): String {
        return delegate.getFirmwareVersion()
    }

    override suspend fun getNtpTime(
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        return delegate.getNtpTime(ntpServer, ntpPort, timeout, timeUnit)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun addToPermanentPowerSaveAllowList(packageName: String) {
        delegate.addToPermanentPowerSaveAllowList(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun removeToPermanentPowerSaveAllowList(packageName: String) {
        delegate.removeToPermanentPowerSaveAllowList(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun isPowerSaveWhitelistApp(packageName: String): Boolean {
        return delegate.isPowerSaveWhitelistApp(packageName)
    }
}