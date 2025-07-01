package com.guodong.android.system.permission.android.pm.pi

import android.content.Context
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver

/**
 * Created by john.wick on 2025/5/27
 */
internal interface IPackageInstaller {

    fun installPackage(context: Context, apkFilePath: String, observer: IPackageInstallObserver)

    fun uninstallPackage(context: Context, packageName: String, observer: IPackageDeleteObserver)
}