package com.guodong.android.system.permission.android.power

import android.content.Context

/**
 * Created by john.wick on 2025/6/30
 */
internal interface IPower {

    companion object {
        internal const val METHOD_GO_TO_SLEEP = "goToSleep"
        internal const val METHOD_WAKE_UP = "wakeUp"

        /**
         * @see [PowerManager#WAKE_REASON_APPLICATION]
         */
        internal const val WAKE_REASON_APPLICATION = 2
    }

    fun goToSleep(context: Context): Boolean

    fun wakeUp(context: Context): Boolean
}