package com.guodong.android.system.permission.android.pm.pi

import android.content.Context
import android.content.pm.IPackageDeleteObserver2
import android.content.pm.IPackageInstallObserver2
import android.content.pm.IPackageManager
import android.net.Uri
import android.os.Bundle
import android.os.ServiceManager
import android.util.Log
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver
import com.guodong.android.system.permission.android.util.PACKAGE_SERVICE
import com.guodong.android.system.permission.android.util.ToString
import com.guodong.android.system.permission.android.util.userId
import java.io.File


/**
 * Created by john.wick on 2025/5/27
 */
internal object PackageInstallerApi24 : IPackageInstaller {

    private const val TAG = "PackageInstallerApi24"

    override fun installPackage(
        context: Context,
        apkFilePath: String,
        observer: IPackageInstallObserver
    ) {
        val pmBinder = ServiceManager.getService(PACKAGE_SERVICE)
        val pms = IPackageManager.Stub.asInterface(pmBinder)
        val packageURI = Uri.fromFile(File(apkFilePath))
        pms.installPackageAsUser(
            packageURI.path,
            object : IPackageInstallObserver2.Stub() {
                override fun onPackageInstalled(
                    basePackageName: String,
                    returnCode: Int,
                    msg: String?,
                    extras: Bundle?
                ) {
                    val isSuccessful =
                        returnCode == 1 /* android.content.pm.PackageManager#INSTALL_SUCCEEDED */
                    Log.d(
                        TAG,
                        "onPackageInstalled: PackageName($basePackageName), Successful($isSuccessful, $returnCode), Message($msg), extras(${extras?.ToString()})"
                    )
                    observer.onPackageInstalled(
                        basePackageName,
                        isSuccessful,
                        returnCode,
                        msg,
                        extras
                    )
                }
            },
            2 or 4096 /* PackageManager.INSTALL_REPLACE_EXISTING or PackageManager.INSTALL_DONT_KILL_APP */,
            context.packageName,
            context.userId
        )
    }

    override fun uninstallPackage(
        context: Context,
        packageName: String,
        observer: IPackageDeleteObserver
    ) {
        val pmBinder = ServiceManager.getService(PACKAGE_SERVICE)
        val pms = IPackageManager.Stub.asInterface(pmBinder)
        pms.deletePackage(packageName, object : IPackageDeleteObserver2.Stub() {
            override fun onPackageDeleted(packageName: String, returnCode: Int, msg: String?) {
                val isSuccessful =
                    returnCode == 1 /* android.content.pm.PackageManager#DELETE_SUCCEEDED */
                Log.d(
                    TAG,
                    "onPackageDeleted: PackageName($packageName), Successful($isSuccessful, $returnCode), $msg"
                )
                observer.onPackageDeleted(packageName, isSuccessful, returnCode, msg, null)
            }
        }, context.userId, 2 /* PackageManager.DELETE_ALL_USERS */)
    }
}