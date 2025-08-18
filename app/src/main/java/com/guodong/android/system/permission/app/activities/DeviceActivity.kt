package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.guodong.android.system.permission.api.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.databinding.ActivityDeviceBinding

/**
 * Created by guodongAndroid on 2025/8/14
 */
class DeviceActivity : BaseActivity<ActivityDeviceBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, DeviceActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityDeviceBinding {
        return ActivityDeviceBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityDeviceBinding.initViews() {
        btnReboot.setOnClickListener {
            AlertDialog.Builder(this@DeviceActivity)
                .setTitle("确认重启？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.reboot()
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnShutdown.setOnClickListener {
            AlertDialog.Builder(this@DeviceActivity)
                .setTitle("确认关机？")
                .setPositiveButton("确认") { dialog, _ ->
                    SystemPermissionCompat.shutdown()
                    dialog.dismiss()
                }.setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnFactoryReset.setOnClickListener {
            Toast.makeText(this@DeviceActivity, "暂未开发", Toast.LENGTH_SHORT).show()
        }
    }
}