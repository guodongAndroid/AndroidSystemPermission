package com.guodong.android.system.permission.android.adb

import android.content.Context
import android.os.SystemProperties
import android.provider.Settings

/**
 * Created by john.wick on 2025/6/30
 */
internal object AdbCompat : IAdb {

    override fun enableAbd(context: Context, enable: Boolean) {
        disableAdb(context)

        if (enable) {
            enableAdb(context)
        }
    }

    override fun isAdbEnabled(context: Context): Boolean {
        val isAdbEnabled = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED,
            IAdb.ADB_SETTING_OFF
        ) == IAdb.ADB_SETTING_OFF

        val isWifiEnabled = Settings.Global.getInt(
            context.contentResolver,
            IAdb.ADB_WIFI_ENABLED,
            IAdb.ADB_SETTING_OFF
        ) == IAdb.ADB_SETTING_OFF

        return isAdbEnabled && isWifiEnabled
    }

    private fun enableAdb(context: Context) {
        Settings.Global.putInt(
            context.contentResolver,
            "development_settings_enabled",
            IAdb.ADB_SETTING_ON
        )

        SystemProperties.set("persist.sys.usb.config", "adb")
        // SystemProperties.set("service.adb.tcp.port", "5555")
        SystemProperties.set("persist.internet_adb_enable", "1")

        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED,
            IAdb.ADB_SETTING_ON
        )

        Settings.Global.putInt(
            context.contentResolver,
            IAdb.ADB_WIFI_ENABLED,
            IAdb.ADB_SETTING_ON
        )
    }

    private fun disableAdb(context: Context) {
        Settings.Global.putInt(
            context.contentResolver,
            "development_settings_enabled",
            IAdb.ADB_SETTING_OFF
        )

        SystemProperties.set("persist.sys.usb.config", "null")
        // SystemProperties.set("service.adb.tcp.port", "null")
        SystemProperties.set("persist.internet_adb_enable", "0")

        Settings.Global.putInt(
            context.contentResolver,
            IAdb.ADB_WIFI_ENABLED,
            IAdb.ADB_SETTING_OFF
        )
        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED,
            IAdb.ADB_SETTING_OFF
        )
    }
}