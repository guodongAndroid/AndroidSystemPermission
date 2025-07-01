package com.guodong.android.system.permission.android.pm.clear

import android.content.Context
import android.content.pm.IPackageDataObserver
import android.content.pm.IPackageManager
import android.os.Process
import android.os.ServiceManager
import android.util.Log
import com.guodong.android.system.permission.IApplicationUserDataCleanObserver
import com.guodong.android.system.permission.android.util.PACKAGE_SERVICE
import com.guodong.android.system.permission.android.util.userId

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
            val binder = ServiceManager.getService(PACKAGE_SERVICE)
            val pms = IPackageManager.Stub.asInterface(binder)

            pms.clearApplicationUserData(
                packageName,
                object : IPackageDataObserver.Stub() {
                    override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {
                        Log.d(TAG, "onRemoveCompleted: $packageName, $succeeded")
                        observer.onApplicationUserDataCleaned(packageName, succeeded)
                    }
                },
                Process.myUserHandle().userId
            )
        } catch (e: Exception) {
            Log.e(TAG, "clearApplicationUserData: $packageName", e)
        }
    }
}