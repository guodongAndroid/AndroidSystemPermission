package com.guodong.android.system.permission.android.screen

import android.content.Context

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IScreenOff {

    fun enableScreenNeverOff(context: Context, enable: Boolean)

    fun isScreenNeverOffEnabled(context: Context): Boolean
}