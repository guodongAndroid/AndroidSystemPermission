package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.databinding.ActivitySystemBarBinding
import com.guodong.android.system.permission.app.openDisplaySettings

/**
 * Created by guodongAndroid on 2025/8/14
 */
class SystemBarActivity : BaseActivity<ActivitySystemBarBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SystemBarActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivitySystemBarBinding {
        return ActivitySystemBarBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivitySystemBarBinding.initViews() {
        btnOpenDisplaySettings.setOnClickListener {
            openDisplaySettings()
        }

        msBar.setOnCheckedChangeListener { _, isChecked ->
            SystemPermissionCompat.enableSystemBar(isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    private fun ActivitySystemBarBinding.refreshUI() {
        msBar.isChecked = SystemPermissionCompat.isSystemBarEnabled()
    }
}