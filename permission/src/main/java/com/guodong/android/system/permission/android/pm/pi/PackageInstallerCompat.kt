package com.guodong.android.system.permission.android.pm.pi

import android.content.Context
import android.os.Build
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver

/**
 * Created by john.wick on 2025/5/27
 */
internal object PackageInstallerCompat : IPackageInstaller {

    override fun installPackage(
        context: Context,
        apkFilePath: String,
        observer: IPackageInstallObserver
    ) {
        super.installPackage(context, apkFilePath, observer)
    }

    override fun uninstallPackage(
        context: Context,
        packageName: String,
        observer: IPackageDeleteObserver
    ) {
        super.uninstallPackage(context, packageName, observer)
    }
}