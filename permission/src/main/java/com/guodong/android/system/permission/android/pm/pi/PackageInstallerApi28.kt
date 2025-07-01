package com.guodong.android.system.permission.android.pm.pi

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.guodong.android.system.permission.IPackageDeleteObserver
import com.guodong.android.system.permission.IPackageInstallObserver
import com.guodong.android.system.permission.android.util.ToString
import com.guodong.android.system.permission.android.pm.LocalIntentReceiver
import java.io.File
import kotlin.concurrent.thread

/**
 * Created by john.wick on 2025/5/27
 */
@RequiresApi(Build.VERSION_CODES.P)
internal object PackageInstallerApi28 : IPackageInstaller {

    private const val TAG = "PackageInstallerApi28"

    @SuppressLint("PrivateApi")
    override fun installPackage(
        context: Context,
        apkFilePath: String,
        observer: IPackageInstallObserver
    ) {
        thread {
            val apkFile = File(apkFilePath)
            if (!apkFile.exists()) {
                Log.d(TAG, "installPackage: $apkFilePath, APK文件不存在")
                observer.onPackageInstalled("", false, -1, "$apkFilePath 文件不存在", null)
                return@thread
            }

            val installer = context.packageManager.packageInstaller
            val params =
                PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

            try {
                val installFlagsField = params.javaClass.getDeclaredField("installFlags")
                installFlagsField.isAccessible = true
                val installFlags = installFlagsField.getInt(params)
                installFlagsField.setInt(
                    params,
                    installFlags or 4096 /* android.content.pm.PackageManager#INSTALL_DONT_KILL_APP */
                )

                val int = installFlagsField.getInt(params)
                Log.d(TAG, "installPackage: installFlags(${int and 4096 == 4096})")
            } catch (e: Exception) {
                Log.d(TAG, "installPackage: 设置[installFlags]失败", e)
                observer.onPackageInstalled("", false, -1, e.message, null)
                return@thread
            }

            val session = try {
                val sessionId = installer.createSession(params)
                installer.openSession(sessionId)
            } catch (e: Exception) {
                Log.d(TAG, "installPackage: 创建或打开Session失败")
                observer.onPackageInstalled("", false, -1, e.message, null)
                return@thread
            }

            session.sinkApk(apkFile)
            val receiver = LocalIntentReceiver()
            session.commit(receiver.getIntentSender())

            val result = receiver.getResult()
            val packageName = result.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)
            val status =
                result.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            val isSuccessful = status == PackageInstaller.STATUS_SUCCESS
            val message = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            val extras = result.extras

            Log.d(
                TAG,
                "installPackage: PackageName($packageName), Successful($isSuccessful, $status), Message($message), extras(${extras?.ToString()})"
            )

            observer.onPackageInstalled(
                packageName.orEmpty(),
                isSuccessful,
                status,
                message,
                extras
            )
        }
    }

    override fun uninstallPackage(
        context: Context,
        packageName: String,
        observer: IPackageDeleteObserver
    ) {
        thread {
            val installer = context.packageManager.packageInstaller
            val receiver = LocalIntentReceiver()
            installer.uninstall(packageName, receiver.getIntentSender())
            val result = receiver.getResult()
            val status =
                result.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            val isSuccessful = status == PackageInstaller.STATUS_SUCCESS
            val message = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            val extras = result.extras

            Log.d(
                TAG,
                "uninstallPackage: PackageName($packageName), Successful($isSuccessful, $status), Message($message), extras(${extras?.ToString()})"
            )

            observer.onPackageDeleted(packageName, isSuccessful, status, message, extras)
        }
    }
}