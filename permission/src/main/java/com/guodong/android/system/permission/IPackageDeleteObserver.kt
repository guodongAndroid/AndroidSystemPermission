package com.guodong.android.system.permission

import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
@WorkerThread
fun interface IPackageDeleteObserver {

    fun onPackageDeleted(
        packageName: String,
        isSuccessful: Boolean,
        status: Int,
        message: String?,
        extras: Bundle?
    )
}