package com.guodong.android.system.permission.android.ota

import android.content.Context
import android.os.Environment
import android.os.RecoverySystem
import android.util.Log
import com.guodong.android.system.permission.IOTAPackageInstallObserver
import java.io.File
import kotlin.concurrent.thread


/**
 * Created by john.wick on 2025/5/27
 */
internal object OTACompat {

    private const val TAG = "OTACompat"

    private const val OTA_FILE_NAME = "update.zip"
    private const val DATA_ROOT = "/data/media/0"
    private val FLASH_ROOT = Environment.getExternalStorageDirectory().absolutePath

    fun installOTAPackage(
        context: Context,
        otaFilePath: String,
        observer: IOTAPackageInstallObserver
    ) {
        thread {
            if (otaFilePath.isEmpty() || otaFilePath.isBlank()) {
                Log.d(TAG, "installOTAPackage: Path($otaFilePath) is empty or blank")
                observer.onPackageInstalled(false, "Path($otaFilePath) 文件路径为空")
                return@thread
            }

            val srcFile = File(otaFilePath)
            if (!srcFile.exists()) {
                Log.e(TAG, "installOTAPackage: OTA文件不存在")
                observer.onPackageInstalled(false, "Path($otaFilePath) 文件不存在")
                return@thread
            }

            if (!srcFile.isFile) {
                Log.e(TAG, "installOTAPackage: Path($otaFilePath) not a file")
                observer.onPackageInstalled(false, "Path($otaFilePath) 不是文件")
                return@thread
            }

            val dest = File(FLASH_ROOT, OTA_FILE_NAME)
            if (dest.exists()) {
                dest.delete()
            }

            val renameTo = srcFile.renameTo(dest)
            Log.d(TAG, "installOTAPackage: rename OTA file to ${dest.name}: $renameTo")

            try {
                RecoverySystem.verifyPackage(dest, {
                    Log.d(TAG, "verifyOTAPackage progress: $it")
                }, null)
            } catch (e: Exception) {
                Log.d(TAG, "verifyOTAPackage: OTA file verify failure", e)
                observer.onPackageInstalled(false, "校验OTA文件失败: ${e.message}")
                return@thread
            }

            try {
                val otaRealPath = getOTARealPath(dest.absolutePath)
                val otaFile = File(otaRealPath)

                Log.d(TAG, "installOTAPackage: start ota, ota file path: $otaRealPath")
                RecoverySystem.installPackage(context, otaFile)
                observer.onPackageInstalled(true, "OTA安装执行成功")
            } catch (e: Exception) {
                Log.e(TAG, "installOTAPackage: failure", e)
                observer.onPackageInstalled(false, "OTA安装执行失败: ${e.message}")
            }
        }
    }

    fun deleteOTAPackage() {
        thread {
            val file = File(Environment.getExternalStorageDirectory(), OTA_FILE_NAME)
            if (!file.exists()) {
                Log.d(TAG, "deleteOTAPackage: OTA文件不存在")
                return@thread
            }

            val delete = file.delete()
            Log.d(TAG, "deleteOTAPackage: Path(${file.absolutePath}) delete: $delete")
        }
    }

    private fun getOTARealPath(packagePath: String): String {
        var newPackagePath = packagePath
        if (packagePath.startsWith(FLASH_ROOT)) {
            newPackagePath = packagePath.replace(FLASH_ROOT, DATA_ROOT)
        }
        Log.d(TAG, "packagePath: $packagePath, realPackagePath: $newPackagePath")
        return newPackagePath
    }
}