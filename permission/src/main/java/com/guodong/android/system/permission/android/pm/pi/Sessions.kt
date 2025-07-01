package com.guodong.android.system.permission.android.pm.pi

import android.content.pm.PackageInstaller.Session
import java.io.File

/**
 * Created by john.wick on 2025/5/27
 */
internal fun Session.sinkApk(apkFile: File) = runCatching {
    this.openWrite("InstallApp.apk", 0, apkFile.length()).use { os ->
        apkFile.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            var read = fis.read(buffer)
            while (read != -1) {
                os.write(buffer, 0, read)
                read = fis.read(buffer)
            }
            this.fsync(os)
        }
    }
}.isSuccess