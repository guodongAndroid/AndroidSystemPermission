package com.guodong.android.system.permission.app

import android.app.Application
import com.guodong.android.system.permission.AndroidStandardSystemPermission
import com.guodong.android.system.permission.SystemPermissionCompat

/**
 * Created by john.wick on 2025/7/1
 */
class PermissionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SystemPermissionCompat.setDelegate(AndroidStandardSystemPermission())
        SystemPermissionCompat.setContext(this)
    }
}