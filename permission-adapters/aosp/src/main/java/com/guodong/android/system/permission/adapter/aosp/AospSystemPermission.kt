package com.guodong.android.system.permission.adapter.aosp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.IPackageDataObserver
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import android.os.SystemClock
import android.os.UserHandleHidden
import android.provider.Settings
import android.util.Log
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.guodong.android.system.permission.adapter.aosp.android.adb.AdbCompat
import com.guodong.android.system.permission.adapter.aosp.android.ota.OTACompat
import com.guodong.android.system.permission.adapter.aosp.android.runtime.RuntimePermissionCompat
import com.guodong.android.system.permission.adapter.aosp.android.sntp.SntpClientCompat
import com.guodong.android.system.permission.api.IApplicationUserDataCleanObserver
import com.guodong.android.system.permission.api.IOTAPackageInstallObserver
import com.guodong.android.system.permission.api.IPackageDeleteObserver
import com.guodong.android.system.permission.api.IPackageInstallObserver
import com.guodong.android.system.permission.api.ISystemPermission
import com.guodong.android.system.permission.api.Vendor
import com.guodong.android.system.permission.api.annotation.Rotation
import com.guodong.android.system.permission.api.domain.NetworkAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.hidden.compat.ActivityManagerApis
import rikka.hidden.compat.AlarmManagerApis
import rikka.hidden.compat.DeviceIdleControllerApis
import rikka.hidden.compat.EthernetManagerApis
import rikka.hidden.compat.LauncherApis
import rikka.hidden.compat.PackageInstallerApis
import rikka.hidden.compat.PackageManagerApis
import rikka.hidden.compat.PowerManagerApis
import rikka.hidden.compat.TakeScreenshotApis
import rikka.hidden.compat.UiModeManagerApis
import java.io.File
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Created by guodongAndroid on 2025/5/27
 */
@Keep
open class AospSystemPermission : ISystemPermission {

    companion object {
        private const val TAG = "AospSystemPermission"

        private const val TEN_MINUTES_MS = 1_000 * 60 * 10
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
        return BuildConfig.SDK_VERSION
    }

    override suspend fun enableEthernet(enable: Boolean) {
        EthernetManagerApis.setEthernetEnabledNoThrow(enable)
    }

    @Suppress("DEPRECATION")
    override suspend fun isEthernetEnabled(): Boolean {
        val manager = context.getSystemService<ConnectivityManager>() ?: return false
        val ni = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
        return ni.isConnected
    }

    override suspend fun setEthernetStaticAddress(
        ipAddress: String,
        netmask: String,
        gateway: String,
        dns1: String,
        dns2: String,
    ): Boolean = try {
        EthernetManagerApis.setStaticAddress(ipAddress, netmask, gateway, dns1, dns2)
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun setEthernetDhcpAddress(): Boolean = try {
        EthernetManagerApis.setDhcpAddress()
        true
    } catch (e: Exception) {
        false
    }

    override suspend fun getEthernetNetworkAddress(): NetworkAddress = try {
        val address = EthernetManagerApis.getNetworkAddress()
        NetworkAddress(
            address.ipAssignment,
            address.address,
            address.netmask,
            address.gateway,
            address.dns1,
            address.dns2
        )
    } catch (e: Exception) {
        NetworkAddress.UNASSIGNED
    }

    override suspend fun getEthernetMacAddress(): String = withContext(Dispatchers.IO) {
        EthernetManagerApis.getMacAddress().orEmpty()
    }

    override fun reboot() {
        PowerManagerApis.rebootNoThrow("")
    }

    override fun shutdown() {
        PowerManagerApis.shutdownNoThrow("")
    }

    override fun factoryReset() {
        TODO("factoryReset")
    }

    override fun grantRuntimePermission(packageName: String): Boolean {
        return RuntimePermissionCompat.grantRuntimePermission(context, packageName)
    }

    override fun getLauncher(): ComponentName? {
        return LauncherApis.getLauncherNoThrow()
    }

    override fun setLauncher(packageName: String): Boolean {
        return LauncherApis.setLauncherNoThrow(context, packageName)
    }

    override fun openSystemLauncher() {
        try {
            context.startActivity(Intent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                component = ComponentName("com.android.launcher3", "com.android.launcher3.Launcher")
            })
        } catch (ignore: Exception) {
            try {
                context.startActivity(Intent().apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    component = ComponentName(
                        "com.android.launcher3",
                        "com.android.launcher3.uioverrides.QuickstepLauncher"
                    )
                })
            } catch (e: Exception) {
                Log.e(TAG, "openSystemLauncher: ${e.message}", e)
            }
        }
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
        val timeout = if (enable) {
            Int.MAX_VALUE
        } else {
            TEN_MINUTES_MS
        }

        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            timeout
        )
    }

    override fun isScreenNeverOffEnabled(): Boolean {
        val screenOffTimeoutMs = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            -1
        )
        return screenOffTimeoutMs == Int.MAX_VALUE
    }

    override fun setScreenOn() {
        PowerManagerApis.wakeUpNoThrow(SystemClock.uptimeMillis())
    }

    override fun setScreenOff() {
        PowerManagerApis.goToSleepNoThrow(SystemClock.uptimeMillis())
    }

    override fun enableScreenAutoRotation(enable: Boolean) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            if (enable) 1 else 0
        )
    }

    override fun isScreenAutoRotationEnabled(): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        ) == 1
    }

    override fun setScreenRotation(@Rotation rotation: Int) {
        if (isScreenAutoRotationEnabled()) {
            enableScreenAutoRotation(false)
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
        try {
            PackageManagerApis.clearApplicationUserData(
                packageName,
                object : IPackageDataObserver.Stub() {
                    override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {
                        Log.d(TAG, "onRemoveCompleted: $packageName, $succeeded")
                        observer.onApplicationUserDataCleaned(packageName, succeeded)
                    }
                },
                UserHandleHidden.myUserId(),
            )
        } catch (e: Exception) {
            Log.e(TAG, "clearApplicationUserData: $packageName", e)
        }
    }

    override fun installPackage(apkFilePath: String, observer: IPackageInstallObserver) {
        PackageInstallerApis.installPackageNoThrow(apkFilePath) { packageName, isSuccessful, status, message, extras ->
            observer.onPackageInstalled(
                packageName,
                isSuccessful,
                status,
                message,
                extras
            )
        }
    }

    override fun uninstallPackage(packageName: String, observer: IPackageDeleteObserver) {
        PackageInstallerApis.uninstallPackageNoThrow(packageName) { observerPackageName, isSuccessful, status, message, extras ->
            observer.onPackageDeleted(observerPackageName, isSuccessful, status, message, extras)
        }
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

    override suspend fun takeScreenShot(savePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(savePath)
        if (file.isDirectory) {
            throw IllegalArgumentException("savePath is directory")
        }

        if (!file.exists()) {
            val parentFile =
                file.parentFile ?: throw IllegalArgumentException("savePath is no parent path")
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }

        val bitmap = takeScreenShot() ?: return@withContext false
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()

        true
    }

    override fun takeScreenShot(): Bitmap? {
        return TakeScreenshotApis.takeScreenshotNoThrow(context)
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun addToPermanentPowerSaveAllowList(packageName: String) {
        DeviceIdleControllerApis.addPowerSaveWhitelistAppNoThrow(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun removeToPermanentPowerSaveAllowList(packageName: String) {
        DeviceIdleControllerApis.removePowerSaveWhitelistAppNoThrow(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun isPowerSaveWhitelistApp(packageName: String): Boolean {
        return DeviceIdleControllerApis.isPowerSaveWhitelistAppNoThrow(packageName)
    }
}