package com.guodong.android.system.permission.android.pm.pi

import android.content.Context
import android.os.Build
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver

/**
 * Created by john.wick on 2025/5/27
 */
internal object PackageInstallerCompat : IPackageInstaller {

    private val installer = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            PackageInstallerApi34
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            PackageInstallerApi28
        }

        else -> {
            PackageInstallerApi25
        }
    }

    override fun installPackage(
        context: Context,
        apkFilePath: String,
        observer: IPackageInstallObserver
    ) {
        installer.installPackage(context, apkFilePath, observer)
    }

    override fun uninstallPackage(
        context: Context,
        packageName: String,
        observer: IPackageDeleteObserver
    ) {
        installer.uninstallPackage(context, packageName, observer)
    }
}