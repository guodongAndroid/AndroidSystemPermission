package com.guodong.android.system.permission.adapter.aosp.android.runtime

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.UserHandleHidden
import android.text.TextUtils
import android.util.Log
import rikka.hidden.compat.AppOpsApis
import rikka.hidden.compat.PermissionManagerApis

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
            val userId = UserHandleHidden.myUserId()
            val packageInfo =
                pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS) ?: return false

            val applicationInfo = packageInfo.applicationInfo ?: return false
            if (applicationInfo.targetSdkVersion < Build.VERSION_CODES.M) {
                return true
            }

            val permissions = packageInfo.requestedPermissions
            if (permissions.isNullOrEmpty()) {
                return false
            }

            val uid = applicationInfo.uid

            for (permission in permissions) {
                Log.d(TAG, "grantRuntimePermission: permissionName -> $permission")

                val pir = runCatching { pm.getPermissionInfo(permission, 0) }
                if (pir.isFailure) {
                    continue
                }

                val pi = pir.getOrNull() ?: continue

                val isDangerousPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pi.protection == PermissionInfo.PROTECTION_DANGEROUS
                } else {
                    @Suppress("DEPRECATION")
                    (pi.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS
                }

                if (isDangerousPermission) {
                    try {
                        PermissionManagerApis.grantRuntimePermission(
                            packageName,
                            permission,
                            userId
                        )
                    } catch (e: Exception) {
                        // 目前只有一台Android13的设备，在此设备上会出现`CAMERA`, `RECORD_AUDIO`等不是可变更权限类型错误，尚不清楚是否仅在此设备上出现
                        val message = e.message.orEmpty()
                        val expectMessage =
                            newNotChangeablePermissionTypeExceptionMessage(packageName, permission)
                        if (message != expectMessage) {
                            throw e
                        }
                    }
                }

                /* code 见 [frameworks/proto_logging/stats/enums/app_shared/app_op_enums.proto] */
                if (TextUtils.equals(permission, Manifest.permission.WRITE_SETTINGS)) {
                    AppOpsApis.setMode(23, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    AppOpsApis.setMode(24, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.CAMERA)) {
                    AppOpsApis.setMode(26, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                    AppOpsApis.setMode(27, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AppOpsApis.setMode(59, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AppOpsApis.setMode(60, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                    AppOpsApis.setMode(66, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }

                if (TextUtils.equals(permission, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    AppOpsApis.setMode(92, uid, packageName, AppOpsManager.MODE_ALLOWED)
                }
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "grantRuntimePermission: $packageName", e)
            false
        }
    }

    private fun newNotChangeablePermissionTypeExceptionMessage(
        packageName: String,
        permName: String
    ): String {
        return "Permission $permName requested by $packageName is not a changeable permission type"
    }
}