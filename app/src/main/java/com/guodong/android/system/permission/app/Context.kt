package com.guodong.android.system.permission.app

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by john.wick on 2025/8/5
 */
val Context.versionName: String
    get() = try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }

val Context.versionCode: Long
    get() = try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        1L
    }