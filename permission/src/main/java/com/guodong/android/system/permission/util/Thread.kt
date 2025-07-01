package com.guodong.android.system.permission.util

import android.os.Looper

/**
 * Created by john.wick on 2025/5/27
 */
fun isMainThread(): Boolean {
    return Looper.myLooper() === Looper.getMainLooper()
}