package com.guodong.android.system.permission

/**
 * Created by john.wick on 2025/6/30
 */
interface IApplicationUserDataCleanObserver {

    fun onApplicationUserDataCleaned(
        packageName: String,
        isSuccessful: Boolean,
    )
}