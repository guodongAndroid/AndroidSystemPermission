package com.guodong.android.system.permission

import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
@WorkerThread
fun interface IOTAPackageInstallObserver {

    fun onPackageInstalled(
        isSuccessful: Boolean,
        message: String,
    )
}