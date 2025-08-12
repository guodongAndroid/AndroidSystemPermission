package com.guodong.android.system.permission.android.adb

import android.content.Context
import android.os.Build
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
        ) == IAdb.ADB_SETTING_ON

        val isWifiEnabled = Settings.Global.getInt(
            context.contentResolver,
            IAdb.ADB_WIFI_ENABLED,
            IAdb.ADB_SETTING_OFF
        ) == IAdb.ADB_SETTING_ON

        return isAdbEnabled && isWifiEnabled
    }

    override fun setAdbPort(port: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isAdbTlsServerEnabled()) {
                SystemProperties.set("persist.adb.tls_server.cusport", port.toString())
                SystemProperties.set("service.adb.tls.port", port.toString())
            }
        }

        SystemProperties.set("service.adb.tcp.port", port.toString())

        SystemProperties.set("ctl.restart", "adbd")
    }

    override fun getAdbPort(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isAdbTlsServerEnabled()) {
                var port = SystemProperties.getInt("service.adb.tls.port", -1)
                if (port != -1) {
                    return port
                }

                port = SystemProperties.getInt("persist.adb.tls_server.cusport", -1)
                if (port != -1) {
                    return port
                }
            }

            return SystemProperties.getInt("service.adb.tcp.port", -1)
        } else {
            return SystemProperties.getInt("service.adb.tcp.port", -1)
        }
    }

    private fun enableAdb(context: Context) {
        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            IAdb.ADB_SETTING_ON
        )

        SystemProperties.set("persist.sys.usb.config", "adb")
        SystemProperties.set("persist.internet_adb_enable", "1")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemProperties.set("persist.adb.tls_server.enable", "1")
        }

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
        /* Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            IAdb.ADB_SETTING_OFF
        ) */

        SystemProperties.set("persist.sys.usb.config", "null")
        SystemProperties.set("persist.internet_adb_enable", "0")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemProperties.set("persist.adb.tls_server.enable", "0")
        }

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

    private fun isAdbTlsServerEnabled(): Boolean {
        return SystemProperties.getInt("persist.adb.tls_server.enable", -1) == 1
    }
}