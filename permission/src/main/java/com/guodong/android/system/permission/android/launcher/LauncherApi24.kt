package com.guodong.android.system.permission.android.launcher

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.IPackageManager
import android.content.pm.ResolveInfo
import android.os.Process
import android.os.ServiceManager
import android.util.Log
import com.guodong.android.system.permission.android.util.PACKAGE_SERVICE
import com.guodong.android.system.permission.android.util.userId
import com.guodong.android.system.permission.util.getAppLaunchComponentName
import com.guodong.android.system.permission.util.getAppName
import com.guodong.android.system.permission.util.getHomeActivities

/**
 * Created by john.wick on 2025/4/15
 */
internal object LauncherApi24 : ILauncher {

    private const val TAG = "LauncherApi24"

    override fun getLauncher(context: Context): ComponentName? {
        val resolveInfos = mutableListOf<ResolveInfo>()
        return context.getHomeActivities(resolveInfos)
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

        val componentNames = resolveInfos.map {
            val activityInfo = it.activityInfo
            ComponentName(activityInfo.packageName, activityInfo.name)
        }

        if (!componentNames.contains(launchComponentName)) {
            val appName = context.getAppName(packageName)
            Log.d(
                TAG, "setLauncher: App($appName)缺少以下两个属性\n" +
                        "<category android:name=\"android.intent.category.HOME\"/>\n" +
                        "<category android:name=\"android.intent.category.DEFAULT\"/>"
            )
            return false
        }

        return try {
            val homeFilter = IntentFilter(Intent.ACTION_MAIN)
            homeFilter.addCategory(Intent.CATEGORY_HOME)
            homeFilter.addCategory(Intent.CATEGORY_DEFAULT)
            homeFilter.addCategory(Intent.CATEGORY_LAUNCHER)

            val binder = ServiceManager.getService(PACKAGE_SERVICE)
            val pms = IPackageManager.Stub.asInterface(binder)
            pms.replacePreferredActivity(
                homeFilter,
                IntentFilter.MATCH_CATEGORY_EMPTY,
                componentNames.toTypedArray(),
                launchComponentName,
                Process.myUserHandle().userId
            )

            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (homeComponentName != null) {
                am.killBackgroundProcesses(homeComponentName.packageName)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}