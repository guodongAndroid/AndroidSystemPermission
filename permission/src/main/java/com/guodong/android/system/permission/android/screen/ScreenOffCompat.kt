package com.guodong.android.system.permission.android.screen

import android.content.Context
import android.provider.Settings

/**
 * Created by john.wick on 2025/5/27
 */
internal object ScreenOffCompat : IScreenOff {

    private const val TEN_MINUTES_MS = 1_000 * 60 * 10

    override fun enableScreenNeverOff(context: Context, enable: Boolean) {
        val timeout = if (enable) {
            Int.MAX_VALUE
        } else {
            TEN_MINUTES_MS
        }

        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                timeout
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isScreenNeverOffEnabled(context: Context): Boolean = synchronized(this) {
        try {
            val screenOffTimeoutMs = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                -1
            )
            screenOffTimeoutMs == Int.MAX_VALUE
        } catch (e: Exception) {
            false
        }
    }
}