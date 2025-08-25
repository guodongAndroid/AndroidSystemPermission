package com.guodong.android.system.permission.app

import android.app.Application
import android.util.Log
import com.guodong.android.system.permission.adapter.signway.SignWaySystemPermission
import com.guodong.android.system.permission.api.SystemPermissionCompat

/**
 * Created by guodongAndroid on 2025/7/1
 */
class PermissionApplication : Application() {

    companion object {
        private const val TAG = "PermissionApplication"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate: currentProcessName: ${SystemPermissionCompat.currentProcessName()}")

        SystemPermissionCompat.setDelegate(SignWaySystemPermission())
        SystemPermissionCompat.setContext(this)
    }
}