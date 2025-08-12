package com.guodong.android.system.permission.android.adb

import android.content.Context
import androidx.annotation.IntRange

/**
 * Created by john.wick on 2025/6/30
 */
internal interface IAdb {

    companion object {
        internal const val ADB_WIFI_ENABLED: String = "adb_wifi_enabled"
        internal const val ADB_SETTING_ON = 1
        internal const val ADB_SETTING_OFF = 0
    }

    fun enableAbd(context: Context, enable: Boolean)

    fun isAdbEnabled(context: Context): Boolean

    fun setAdbPort(@IntRange(from = 5000, to = 65535)port: Int)

    @IntRange(from = 5000, to = 65535)
    fun getAdbPort(): Int
}