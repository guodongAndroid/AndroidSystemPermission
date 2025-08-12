package com.guodong.android.system.permission.android.pm.clear

import android.content.Context
import android.content.pm.IPackageDataObserver
import android.os.UserHandleHidden
import android.util.Log
import com.guodong.android.system.permission.IApplicationUserDataCleanObserver
import rikka.hidden.compat.PackageManagerApis

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IClearApplicationUserData {

    companion object {
        private const val TAG = "IClearApplicationUserData"
    }

    fun clearApplicationUserData(
        context: Context,
        packageName: String,
        observer: IApplicationUserDataCleanObserver
    ) {
        try {
            PackageManagerApis.clearApplicationUserData(
                packageName,
                object : IPackageDataObserver.Stub() {
                    override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {
                        Log.d(TAG, "onRemoveCompleted: $packageName, $succeeded")
                        observer.onApplicationUserDataCleaned(packageName, succeeded)
                    }
                },
                UserHandleHidden.myUserId(),
            )
        } catch (e: Exception) {
            Log.e(TAG, "clearApplicationUserData: $packageName", e)
        }
    }
}