package com.guodong.android.system.permission

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.content.getSystemService
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
import com.guodong.android.system.permission.annotation.Orientation
import com.guodong.android.system.permission.domain.NetworkAddress
import com.guodong.android.system.permission.util.ShellUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/**
 * Created by john.wick on 2025/5/27
 */
open class AndroidStandardSystemPermission : ISystemPermission {

    companion object {
        private const val TAG = "AndroidStandardSystemPermission"
    }

    protected lateinit var context: Context
        private set

    override fun setContext(context: Context) {
        this.context = context.applicationContext
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
        return EthernetCompat.setStaticAddress(context, ipAddress, netmask, gateway, dns1, dns2)
    }

    override suspend fun setEthernetDhcpAddress(): Boolean {
        return EthernetCompat.setDhcpAddress(context)
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return EthernetCompat.getNetworkAddress(context)
    }

    override suspend fun getEthernetMacAddress(): String {
        return EthernetCompat.getMacAddress()
    }

    override fun reboot(): Boolean {
        return try {
            val manager = context.getSystemService<PowerManager>() ?: return false
            manager.reboot("")
            true
        } catch (e: Exception) {
            Log.e(TAG, "reboot: ${e.message}", e)
            false
        }
    }

    override fun factoryReset() {
        TODO("factoryReset")
    }

    override fun grantRuntimePermission(packageName: String): Boolean {
        return RuntimePermissionCompat.grantRuntimePermission(context, packageName)
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

    override fun setScreenBright(level: Int) {
        enableAutoBrightness(false)

        val brightness = ceil(level * 1F / 100 * 255).toInt()

        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            brightness
        )
    }

    override fun getScreenBright(): Int {
        val brightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            75
        )
        return ceil(brightness * 100F / 255).toInt()
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

    override fun enableScreenNeverOff(enable: Boolean) {
        ScreenOffCompat.enableScreenNeverOff(context, enable)
    }

    override fun isScreenNeverOffEnabled(): Boolean {
        return ScreenOffCompat.isScreenNeverOffEnabled(context)
    }

    override fun setScreenOn() {
        PowerCompat.wakeUp(context)
    }

    override fun setScreenOff() {
        PowerCompat.goToSleep(context)
    }

    override fun enableAdb(enable: Boolean) {
        AdbCompat.enableAbd(context, enable)
    }

    override fun isAdbEnabled(): Boolean {
        return AdbCompat.isAdbEnabled(context)
    }

    override fun hideSystemBar(): Boolean {
        // AOSP not supported
        return false
    }

    override fun showSystemBar(): Boolean {
        // AOSP not supported
        return false
    }

    override suspend fun setDate(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val timeInMillis = calendar.timeInMillis
        return SystemClock.setCurrentTimeMillis(timeInMillis)
    }

    override suspend fun setTime(hour: Int, minute: Int, second: Int): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }

        val timeInMillis = calendar.timeInMillis
        return SystemClock.setCurrentTimeMillis(timeInMillis)
    }

    override fun setOrientation(angle: Int) {
        val rotation = when (angle) {
            Orientation.ORIENTATION_90 -> 1
            Orientation.ORIENTATION_180 -> 2
            Orientation.ORIENTATION_270 -> 3
            else -> 0
        }

        ShellUtil.execCmd("settings put system user_roration $rotation")
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