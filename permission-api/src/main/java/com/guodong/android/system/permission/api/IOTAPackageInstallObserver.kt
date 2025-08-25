package com.guodong.android.system.permission.api

import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by guodongAndroid on 2025/5/27
 */
@Keep
@WorkerThread
fun interface IOTAPackageInstallObserver {

    fun onPackageInstalled(
        isSuccessful: Boolean,
        message: String,
    )
}