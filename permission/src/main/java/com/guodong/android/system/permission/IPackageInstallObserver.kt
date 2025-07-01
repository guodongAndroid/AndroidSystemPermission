package com.guodong.android.system.permission

import android.os.Bundle
import androidx.annotation.Keep

/**
 * Created by john.wick on 2025/5/27
 */
@Keep
interface IPackageInstallObserver {

    fun onPackageInstalled(
        packageName: String,
        isSuccessful: Boolean,
        status: Int,
        message: String?,
        extras: Bundle?
    )
}