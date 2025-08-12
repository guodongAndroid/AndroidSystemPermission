package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.databinding.ActivityTakeScreenShotBinding

/**
 * Created by guodongAndroid on 2025/8/13
 */
class TakeScreenshotActivity : BaseActivity<ActivityTakeScreenShotBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TakeScreenshotActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityTakeScreenShotBinding {
        return ActivityTakeScreenShotBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityTakeScreenShotBinding.initViews() {
        btnTake.setOnClickListener {
            SystemPermissionCompat.takeScreenShot()?.let {
                ivScreenShot.setImageBitmap(it)
            }
        }
    }

}