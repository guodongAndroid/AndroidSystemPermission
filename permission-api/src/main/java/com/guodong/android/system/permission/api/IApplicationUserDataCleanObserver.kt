package com.guodong.android.system.permission.api

import androidx.annotation.Keep
import androidx.annotation.WorkerThread

/**
 * Created by guodongAndroid on 2025/6/30
 */
@Keep
@WorkerThread
fun interface IApplicationUserDataCleanObserver {

    fun onApplicationUserDataCleaned(
        packageName: String,
        isSuccessful: Boolean,
    )
}