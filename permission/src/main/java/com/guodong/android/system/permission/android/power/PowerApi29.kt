package com.guodong.android.system.permission.android.power

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

/**
 * Created by john.wick on 2025/6/30
 */
@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("PrivateApi")
internal object PowerApi29 : IPower {

    private const val TAG = "PowerApi29"

    override fun goToSleep(context: Context): Boolean {
        val manager = context.getSystemService<PowerManager>() ?: return false
        val clazz = manager.javaClass
        return try {
            val method =
                clazz.getDeclaredMethod(IPower.METHOD_GO_TO_SLEEP, Long::class.java)
            method.isAccessible = true
            method.invoke(manager, SystemClock.uptimeMillis())
            true
        } catch (e: Exception) {
            Log.e(TAG, "goToSleep: ${e.message}", e)
            false
        }
    }

    override fun wakeUp(context: Context): Boolean {
        val manager = context.getSystemService<PowerManager>() ?: return false
        val clazz = manager.javaClass
        return try {
            val method =
                clazz.getDeclaredMethod(IPower.METHOD_WAKE_UP, Long::class.java, Int::class.java, String::class.java)
            method.isAccessible = true
            method.invoke(manager, SystemClock.uptimeMillis(), IPower.WAKE_REASON_APPLICATION, IPower.METHOD_WAKE_UP)
            true
        } catch (e: Exception) {
            Log.e(TAG, "wakeUp: ${e.message}", e)
            false
        }
    }
}