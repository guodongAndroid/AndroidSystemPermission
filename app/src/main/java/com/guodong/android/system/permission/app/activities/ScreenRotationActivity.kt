package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.annotation.Rotation
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityScreenRotationBinding
import com.guodong.android.system.permission.app.openDisplaySettings

/**
 * Created by guodongAndroid on 2025/8/14
 */
class ScreenRotationActivity : BaseActivity<ActivityScreenRotationBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ScreenRotationActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityScreenRotationBinding {
        return ActivityScreenRotationBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityScreenRotationBinding.initViews() {
        btnOpenDisplaySettings.setOnClickListener {
            openDisplaySettings()
        }

        msAutoRotation.setOnCheckedChangeListener { _, isChecked ->
            SystemPermissionCompat.enableAutoScreenRotation(isChecked)
            refreshRotationTVUI()
            refreshRotationBtnStateUI()
        }

        btnDegree0.setOnClickListener {
            SystemPermissionCompat.setScreenRotation(Rotation.ROTATION_0)
        }

        btnDegree90.setOnClickListener {
            SystemPermissionCompat.setScreenRotation(Rotation.ROTATION_90)
        }

        btnDegree180.setOnClickListener {
            SystemPermissionCompat.setScreenRotation(Rotation.ROTATION_180)
        }

        btnDegree270.setOnClickListener {
            SystemPermissionCompat.setScreenRotation(Rotation.ROTATION_270)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.refreshUI()
    }

    private fun ActivityScreenRotationBinding.refreshUI() {
        refreshSwitchUI()
        refreshRotationTVUI()
        refreshRotationBtnStateUI()
    }

    private fun ActivityScreenRotationBinding.refreshSwitchUI() {
        msAutoRotation.isChecked = SystemPermissionCompat.isAutoScreenRotationEnabled()
    }

    private fun ActivityScreenRotationBinding.refreshRotationTVUI() {
        val degree = when (SystemPermissionCompat.getScreenRotation()) {
            Rotation.ROTATION_180 -> getString(R.string.degree_180)
            Rotation.ROTATION_270 -> getString(R.string.degree_270)
            Rotation.ROTATION_90 -> getString(R.string.degree_90)
            else -> getString(R.string.degree_0)
        }
        tvDegree.text =
            getString(R.string.current_screen_rotation, degree)
    }

    private fun ActivityScreenRotationBinding.refreshRotationBtnStateUI() {
        val isEnabled = !msAutoRotation.isChecked
        btnDegree0.isEnabled = isEnabled
        btnDegree90.isEnabled = isEnabled
        btnDegree180.isEnabled = isEnabled
        btnDegree270.isEnabled = isEnabled
    }
}