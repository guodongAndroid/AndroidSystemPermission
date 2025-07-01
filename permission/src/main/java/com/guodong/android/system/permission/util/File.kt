package com.guodong.android.system.permission.util

import java.io.File

/**
 * Created by john.wick on 2025/5/27
 */

fun deleteDir(dir: File?): Boolean {
    if (dir == null || !dir.exists()) {
        return true
    }

    if (!dir.isDirectory) {
        return dir.delete()
    }

    val children = dir.list() ?: return false
    for (child in children) {
        deleteDir(File(dir, child))
    }

    return false
}