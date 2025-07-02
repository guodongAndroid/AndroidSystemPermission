package com.guodong.android.system.permission.android.launcher

import android.app.role.IRoleManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ResolveInfo
import android.os.Binder
import android.os.Build
import android.os.Process
import android.os.RemoteCallback
import android.os.ServiceManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.guodong.android.system.permission.android.util.userId
import com.guodong.android.system.permission.util.RunSuspend
import com.guodong.android.system.permission.util.getAppLaunchComponentName
import com.guodong.android.system.permission.util.getAppName
import com.guodong.android.system.permission.util.getHomeActivities
import java.util.concurrent.Executors

/**
 * Created by john.wick on 2025/4/15
 */
@RequiresApi(Build.VERSION_CODES.Q)
internal object LauncherApi29 : ILauncher {

    private const val TAG = "LauncherApi29"

    override fun getLauncher(context: Context): ComponentName? {
        return LauncherApi25.getLauncher(context)
    }

    override fun setLauncher(context: Context, packageName: String): Boolean {
        val launchComponentName = context.getAppLaunchComponentName(packageName)
        if (launchComponentName == null) {
            val appName = context.getAppName(packageName)
            Log.d(
                TAG, "setLauncher: App($appName)缺少以下两个属性\n" +
                        "<category android:name=\"android.intent.category.HOME\"/>\n" +
                        "<category android:name=\"android.intent.category.DEFAULT\"/>"
            )
            return false
        }

        val resolveInfos = mutableListOf<ResolveInfo>()
        val homeComponentName = context.getHomeActivities(resolveInfos)
        if (homeComponentName != null && homeComponentName == launchComponentName) {
            return true
        }

        if (resolveInfos.isEmpty()) {
            return false
        }

        return try {
            val suspend = RunSuspend<Boolean>()
            val executor = Executors.newSingleThreadExecutor()
            val callback = RemoteCallback {
                executor.execute {
                    val successful = it != null
                    val token = Binder.clearCallingIdentity()
                    try {
                        Log.d(TAG, "setLauncher: $successful")
                        suspend.resumeWith(successful)
                        executor.shutdown()
                    } finally {
                        Binder.restoreCallingIdentity(token)
                    }
                }
            }

            val binder = ServiceManager.getService(Context.ROLE_SERVICE)
            val rms = IRoleManager.Stub.asInterface(binder)
            rms.addRoleHolderAsUser(
                RoleManager.ROLE_HOME,
                launchComponentName.packageName,
                0,
                Process.myUserHandle().userId,
                callback
            )

            suspend.await()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}