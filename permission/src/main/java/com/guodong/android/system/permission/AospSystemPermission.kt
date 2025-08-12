package com.guodong.android.system.permission

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.os.UserHandleHidden
import android.provider.Settings
import android.util.Log
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.guodong.android.system.permission.android.adb.AdbCompat
import com.guodong.android.system.permission.android.ethernet.EthernetCompat
import com.guodong.android.system.permission.android.launcher.LauncherCompat
import com.guodong.android.system.permission.android.ota.OTACompat
import com.guodong.android.system.permission.android.pm.clear.ClearApplicationUserDataCompat
import com.guodong.android.system.permission.android.pm.pi.PackageInstallerCompat
import com.guodong.android.system.permission.android.power.PowerCompat
import com.guodong.android.system.permission.android.runtime.RuntimePermissionCompat
import com.guodong.android.system.permission.android.sceenshot.TakeScreenShotCompat
import com.guodong.android.system.permission.android.screen.ScreenOffCompat
import com.guodong.android.system.permission.android.sntp.SntpClientCompat
import com.guodong.android.system.permission.annotation.Rotation
import com.guodong.android.system.permission.domain.NetworkAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.hidden.compat.ActivityManagerApis
import rikka.hidden.compat.AlarmManagerApis
import rikka.hidden.compat.UiModeManagerApis
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
open class AospSystemPermission : ISystemPermission {

    companion object {
        private const val TAG = "AospSystemPermission"
    }

    protected lateinit var context: Context
        private set

    override fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    @Vendor
    override fun getVendor(): String {
        return Vendor.AOSP
    }

    override fun getVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    override suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean {
        return EthernetCompat.setStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
    }

    override suspend fun setEthernetDhcpAddress(): Boolean {
        return EthernetCompat.setDhcpAddress()
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return EthernetCompat.getNetworkAddress()
    }

    override suspend fun getEthernetMacAddress(): String {
        return EthernetCompat.getMacAddress()
    }

    override fun reboot() {
        PowerCompat.reboot()
    }

    override fun shutdown() {
        PowerCompat.shutdown()
    }

    override fun factoryReset() {
        TODO("factoryReset")
    }

    override fun grantRuntimePermission(packageName: String): Boolean {
        return RuntimePermissionCompat.grantRuntimePermission(context, packageName)
    }

    override fun getLauncher(): ComponentName? {
        return LauncherCompat.getLauncher(context)
    }

    override fun setLauncher(packageName: String): Boolean {
        return LauncherCompat.setLauncher(context, packageName)
    }

    override fun openSystemLauncher() {
        LauncherCompat.openSystemLauncher(context)
    }

    override fun openSystemSettings() {
        try {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "openSystemSettings: ${e.message}", e)
        }
    }

    override fun openSystemDevelopmentSettings() {
        try {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "openSystemDevelopmentSettings: ${e.message}", e)
        }
    }

    override fun setScreenBrightness(level: Int) {
        enableAutoBrightness(false)

        val brightness = floor(level * 1F / 100 * 255).toInt().coerceIn(1, 255)

        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            brightness
        )
    }

    override fun getScreenBrightness(): Int {
        val brightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            75
        )
        return ceil(brightness * 100F / 255).toInt().coerceIn(1, 100)
    }

    override fun enableAutoBrightness(enable: Boolean): Boolean {
        return try {
            val mode = if (enable) {
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } else {
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            }

            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                mode
            )

            true
        } catch (e: Exception) {
            Log.e(TAG, "enableAutoBrightness: ${e.message}", e)
            false
        }
    }

    override fun isAutoBrightnessEnabled(): Boolean {
        return try {
            val mode = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
            mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (e: Exception) {
            Log.e(TAG, "isAutoBrightnessEnabled: ${e.message}", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun enableDarkUI(enable: Boolean): Boolean {
        return UiModeManagerApis.setNightModeActivatedNoThrow(enable)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun isDarkUIEnabled(): Boolean {
        return UiModeManagerApis.isNightModeActivated()
    }

    override fun enableScreenNeverOff(enable: Boolean) {
        ScreenOffCompat.enableScreenNeverOff(context, enable)
    }

    override fun isScreenNeverOffEnabled(): Boolean {
        return ScreenOffCompat.isScreenNeverOffEnabled(context)
    }

    override fun setScreenOn() {
        PowerCompat.wakeUp()
    }

    override fun setScreenOff() {
        PowerCompat.goToSleep()
    }

    override fun enableAutoScreenRotation(enable: Boolean) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            if (enable) 1 else 0
        )
    }

    override fun isAutoScreenRotationEnabled(): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        ) == 1
    }

    override fun setScreenRotation(@Rotation rotation: Int) {
        if (isAutoScreenRotationEnabled()) {
            enableAutoScreenRotation(false)
        }
        Settings.System.putInt(context.contentResolver, Settings.System.USER_ROTATION, rotation)
    }

    @Rotation
    override fun getScreenRotation(): Int {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.USER_ROTATION,
            Rotation.ROTATION_0
        )
    }

    override fun enableAdb(enable: Boolean) {
        AdbCompat.enableAbd(context, enable)
    }

    override fun isAdbEnabled(): Boolean {
        return AdbCompat.isAdbEnabled(context)
    }

    override fun setAdbPort(@IntRange(from = 5000, to = 65535) port: Int) {
        AdbCompat.setAdbPort(port)
    }

    @IntRange(from = 5000, to = 65535)
    override fun getAdbPort(): Int {
        return AdbCompat.getAdbPort()
    }

    override fun enableSystemBar(enable: Boolean) {
        // AOSP not support
    }

    override fun isSystemBarEnabled(): Boolean {
        // AOSP not supported
        return false
    }

    override suspend fun setTimeZone(timeZone: String): Boolean {
        Settings.Global.putInt(context.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0)
        return AlarmManagerApis.setTimeZoneNoThrow(timeZone)
    }

    override suspend fun setDate(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val timeInMillis = calendar.timeInMillis

        Settings.Global.putInt(context.contentResolver, Settings.Global.AUTO_TIME, 0)
        return SystemClock.setCurrentTimeMillis(timeInMillis)
    }

    override suspend fun setTime(hour: Int, minute: Int, second: Int): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }

        val timeInMillis = calendar.timeInMillis

        Settings.Global.putInt(context.contentResolver, Settings.Global.AUTO_TIME, 0)
        return SystemClock.setCurrentTimeMillis(timeInMillis)
    }

    override suspend fun enableTimeFormat24H(enable: Boolean): Boolean {
        return Settings.System.putString(
            context.contentResolver,
            Settings.System.TIME_12_24,
            if (enable) "24" else "12"
        )
    }

    override suspend fun isTimeFormat24HEnabled(): Boolean {
        val time24 = Settings.System.getString(context.contentResolver, Settings.System.TIME_12_24)
            ?: return false
        return "24" == time24
    }

    override fun clearApplicationUserData(
        packageName: String,
        observer: IApplicationUserDataCleanObserver
    ) {
        ClearApplicationUserDataCompat.clearApplicationUserData(context, packageName, observer)
    }

    override fun installPackage(apkFilePath: String, observer: IPackageInstallObserver) {
        PackageInstallerCompat.installPackage(context, apkFilePath, observer)
    }

    override fun uninstallPackage(packageName: String, observer: IPackageDeleteObserver) {
        PackageInstallerCompat.uninstallPackage(context, packageName, observer)
    }

    override fun killBackgroundProcesses(packageName: String) {
        ActivityManagerApis.killBackgroundProcessesNoThrow(packageName, UserHandleHidden.myUserId())
    }

    override fun forceStopPackage(packageName: String) {
        ActivityManagerApis.forceStopPackageNoThrow(packageName, UserHandleHidden.myUserId())
    }

    override fun installOTAPackage(otaFilePath: String, observer: IOTAPackageInstallObserver) {
        OTACompat.installOTAPackage(context, otaFilePath, observer)
    }

    override suspend fun takeScreenShot(savePath: String): Boolean {
        return TakeScreenShotCompat.takeScreenShot(context, savePath)
    }

    override fun takeScreenShot(): Bitmap? {
        return TakeScreenShotCompat.takeScreenShot(context)
    }

    override suspend fun getFirmwareVersion(): String {
        return Build.DISPLAY
    }

    override suspend fun getNtpTime(
        ntpServer: String,
        ntpPort: Int,
        timeout: Long,
        timeUnit: TimeUnit
    ): Long {
        return withContext(Dispatchers.Default) {
            SntpClientCompat.getNtpTime(context, ntpServer, ntpPort, timeout, timeUnit)
        }
    }
}