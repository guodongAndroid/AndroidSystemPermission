package com.guodong.android.system.permission.android.screen

import android.content.Context

/**
 * Created by john.wick on 2025/5/27
 */
internal object ScreenOffCompat : IScreenOff {

    override fun enableScreenNeverOff(context: Context, enable: Boolean) {
        super.enableScreenNeverOff(context, enable)
    }

    override fun isScreenNeverOffEnabled(context: Context): Boolean {
        return super.isScreenNeverOffEnabled(context)
    }
}