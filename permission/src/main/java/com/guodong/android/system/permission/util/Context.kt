package com.guodong.android.system.permission.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.text.TextUtils

/**
 * Created by john.wick on 2025/5/27
 */
internal fun Context.getAppLaunchComponentName(packageName: String): ComponentName? {
    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
        addCategory(Intent.CATEGORY_DEFAULT)
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfos = packageManager.queryIntentActivities(homeIntent, 0)
    if (resolveInfos.isEmpty()) {
        return null
    }

    return resolveInfos.filter {
        val applicationInfo = it.activityInfo.applicationInfo
        TextUtils.equals(applicationInfo.packageName, packageName)
    }.map { ComponentName(it.activityInfo.packageName, it.activityInfo.name) }.firstOrNull()
}

internal fun Context.getAppName(packageName: String): String {
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    return packageManager.getApplicationLabel(applicationInfo).toString()
}

internal fun Context.getHomeActivities(outs: MutableList<ResolveInfo>): ComponentName? {
    return try {
        val clazz = packageManager.javaClass
        val getHomeActivities = clazz.getMethod("getHomeActivities", MutableList::class.java)
        getHomeActivities.isAccessible = true
        getHomeActivities.invoke(packageManager, outs) as? ComponentName
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}