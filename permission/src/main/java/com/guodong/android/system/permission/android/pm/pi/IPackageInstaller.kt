package com.guodong.android.system.permission.android.pm.pi

import android.content.Context
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver
import rikka.hidden.compat.PackageInstallerApis

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IPackageInstaller {

    fun installPackage(context: Context, apkFilePath: String, observer: IPackageInstallObserver) {
        PackageInstallerApis.installPackageNoThrow(apkFilePath) { packageName, isSuccessful, status, message, extras ->
            observer.onPackageInstalled(
                packageName,
                isSuccessful,
                status,
                message,
                extras
            )
        }
    }

    fun uninstallPackage(context: Context, packageName: String, observer: IPackageDeleteObserver) {
        PackageInstallerApis.uninstallPackageNoThrow(packageName) { observerPackageName, isSuccessful, status, message, extras ->
            observer.onPackageDeleted(observerPackageName, isSuccessful, status, message, extras)
        }
    }
}