package com.guodong.android.system.permission.api

import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
@WorkerThread
fun interface IPackageInstallObserver {

    fun onPackageInstalled(
        packageName: String,
        isSuccessful: Boolean,
        status: Int,
        message: String?,
        extras: Bundle?
    )
}