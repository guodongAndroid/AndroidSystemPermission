package com.guodong.android.system.permission.android.runtime

import android.Manifest.permission
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Process
import android.os.ServiceManager
import android.text.TextUtils
import android.util.Log
import com.android.internal.app.IAppOpsService
import com.guodong.android.system.permission.android.util.PACKAGE_SERVICE
import com.guodong.android.system.permission.android.util.userId

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IRuntimePermission {

    companion object {
        private const val TAG = "IRuntimePermission"
    }

    fun grantRuntimePermission(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager

            val packageInfo: PackageInfo =
                pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val applicationInfo = packageInfo.applicationInfo ?: return false
            if (applicationInfo.targetSdkVersion < Build.VERSION_CODES.M) {
                return true
            }

            val permissions = packageInfo.requestedPermissions
            if (permissions.isNullOrEmpty()) {
                return false
            }

            val uid = applicationInfo.uid

            val opsBinder = ServiceManager.getService(Context.APP_OPS_SERVICE)
            val opss = IAppOpsService.Stub.asInterface(opsBinder)

            val pmBinder = ServiceManager.getService(PACKAGE_SERVICE)
            val pms = IPackageManager.Stub.asInterface(pmBinder)

            permissions.forEach {
                val permissionInfo = pm.getPermissionInfo(it, 0)
                val protectionLevel = permissionInfo.protectionLevel

                if ((protectionLevel and PermissionInfo.PROTECTION_MASK_BASE)
                    == PermissionInfo.PROTECTION_DANGEROUS
                ) {
                    pms.grantRuntimePermission(packageName, it, Process.myUserHandle().userId)
                }

                /* code 见 [frameworks/proto_logging/stats/enums/app_shared/app_op_enums.proto] */
                if (TextUtils.equals(it, permission.WRITE_SETTINGS)) {
                    opss.setMode(23, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(it, permission.SYSTEM_ALERT_WINDOW)) {
                    opss.setMode(24, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(it, permission.MANAGE_EXTERNAL_STORAGE)) {
                    opss.setMode(92, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "grantRuntimePermission: $packageName", e)
            false
        }
    }
}