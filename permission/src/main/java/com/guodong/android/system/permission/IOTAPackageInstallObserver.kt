package com.guodong.android.system.permission

import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
interface IOTAPackageInstallObserver {

    fun onPackageInstalled(
        isSuccessful: Boolean,
        message: String,
    )
}