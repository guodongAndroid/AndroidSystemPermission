package com.guodong.android.system.permission

import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by john.wick on 2025/6/30
 */
@Keep
@WorkerThread
fun interface IApplicationUserDataCleanObserver {

    fun onApplicationUserDataCleaned(
        packageName: String,
        isSuccessful: Boolean,
    )
}