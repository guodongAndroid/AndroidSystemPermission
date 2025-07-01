package com.guodong.android.system.permission.util

import android.util.Log

/**
 * Created by john.wick on 2025/5/27
 */
object AdbdUtil {
    private val TAG: String = AdbdUtil::class.java.simpleName

    private const val STOP_ADBD = "stop adbd"
    private const val START_ADBD = "start adbd"
    private const val SET_PORT = "setprop service.adb.tcp.port "
    private const val GET_PORT = "getprop service.adb.tcp.port"
    private const val SET_ADB = "setprop persist.sys.usb.config adb"

    private const val DEFAULT_PORT = 5555

    fun enableAdbdDefaultPort() {
        enableAdbdPort(DEFAULT_PORT)
    }

    fun enableAdbdPort(port: Int) {
        if (!canUpdateAdbdPort(port)) {
            return
        }

        Log.i(TAG, "setPort --> $port")

        val setPortCmd = SET_PORT + port
        ShellUtil.execCmd(setPortCmd)
        ShellUtil.execCmd(SET_ADB)

        ShellUtil.execCmd(STOP_ADBD)
        ShellUtil.execCmd(START_ADBD)
    }

    private fun canUpdateAdbdPort(port: Int): Boolean {
        val result: ShellUtil.CommandResult = ShellUtil.execCmd(GET_PORT)
        val successMsg: String? = result.successMsg

        Log.i(TAG, "getPort --> $successMsg")

        if (successMsg != null) {
            var hasPort = 0
            try {
                hasPort = successMsg.toInt()
            } catch (ignored: Exception) {
            }

            return hasPort == 0 || hasPort != port
        }
        return true
    }
}