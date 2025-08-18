package com.guodong.android.system.permission.app

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.guodong.android.system.permission.app.model.ApplicationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by john.wick on 2025/8/5
 */
fun Context.getVersionName(packageName: String): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

fun Context.getVersionCode(packageName: String): Long {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1L
    }
}

suspend fun Context.getApplicationModels(): List<ApplicationModel> = withContext(Dispatchers.IO) {
    val modes = mutableListOf<ApplicationModel>()
    val pm = packageManager
    val packages = pm.getInstalledPackages(0)
    for (pkg in packages) {
        val packageName = pkg.packageName
        val model = getApplicationModel(packageName) ?: continue
        modes.add(model)
    }
    modes.sortedBy { it.isSystem }
}

fun Context.getApplicationModel(packageName: String): ApplicationModel? {
    val pm = packageManager
    return try {
        val pkg = pm.getPackageInfo(packageName, 0)
        val ai = pkg.applicationInfo ?: return null
        val icon = ai.loadIcon(pm)
        val name = ai.loadLabel(pm).toString()
        val version = pkg.versionName ?: "Unknown"
        val isSystem = ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
        ApplicationModel(pkg.packageName, icon, name, version, isSystem)
    } catch (e: Exception) {
        null
    }
}

fun Context.openDisplaySettings() {
    val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openHomeSettings() {
    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openApplicationDetailsSettings(packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.fromParts("package", packageName, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openDateSettings() {
    val intent = Intent(Settings.ACTION_DATE_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.M)
fun Context.openBatteryOptimizationsSettings() {
    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}