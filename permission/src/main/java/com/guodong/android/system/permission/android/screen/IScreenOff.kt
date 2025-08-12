package com.guodong.android.system.permission.android.screen

import android.content.Context
import android.provider.Settings

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IScreenOff {

    companion object {
        private const val TEN_MINUTES_MS = 1_000 * 60 * 10
    }

    fun enableScreenNeverOff(context: Context, enable: Boolean) {
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

    fun isScreenNeverOffEnabled(context: Context): Boolean {
        val screenOffTimeoutMs = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            -1
        )
        return screenOffTimeoutMs == Int.MAX_VALUE
    }
}