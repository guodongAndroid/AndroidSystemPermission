package com.guodong.android.system.permission.app

import android.app.Application
import android.util.Log
import com.guodong.android.system.permission.adapter.aosp.AospSystemPermission
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

        SystemPermissionCompat.setDelegate(AospSystemPermission())
        SystemPermissionCompat.setContext(this)
    }
}