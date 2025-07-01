package com.guodong.android.system.permission.android.pm.clear

import android.content.Context
import com.guodong.android.system.permission.IApplicationUserDataCleanObserver

/**
 * Created by john.wick on 2025/5/27
 */
internal object ClearApplicationUserDataCompat : IClearApplicationUserData {

    override fun clearApplicationUserData(
        context: Context,
        packageName: String,
        observer: IApplicationUserDataCleanObserver
    ) {
       super.clearApplicationUserData(context, packageName, observer)
    }
}