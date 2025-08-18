package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.api.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityScreenBrightnessBinding
import com.guodong.android.system.permission.app.openDisplaySettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by guodongAndroid on 2025/8/14
 */
class ScreenBrightnessActivity : BaseActivity<ActivityScreenBrightnessBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ScreenBrightnessActivity::class.java))
        }
    }

    override fun getViewBinding(): ActivityScreenBrightnessBinding {
        return ActivityScreenBrightnessBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityScreenBrightnessBinding.initViews() {
        btnOpenDisplaySettings.setOnClickListener {
            openDisplaySettings()
        }

        btnScreenOffAndOn.setOnClickListener {
            lifecycleScope.launch {
                SystemPermissionCompat.setScreenOff()
                delay(10_000L)
                SystemPermissionCompat.setScreenOn()
            }
        }

        btnScreenOff.setOnClickListener {
            SystemPermissionCompat.setScreenOff()
        }

        btnScreenOn.setOnClickListener {
            SystemPermissionCompat.setScreenOn()
        }

        msUiMode.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                SystemPermissionCompat.enableDarkUI(isChecked)
            }
        }

        msNeverOff.setOnCheckedChangeListener { _, isChecked ->
            SystemPermissionCompat.enableScreenNeverOff(isChecked)
        }

        msAutoBrightness.setOnCheckedChangeListener { _, isChecked ->
            SystemPermissionCompat.enableAutoBrightness(isChecked)
            refreshSliderUI()
        }

        slider.addOnChangeListener { _, value, _ ->
            SystemPermissionCompat.setScreenBrightness(value.toInt())
            refreshBrightnessTVUI()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    private fun ActivityScreenBrightnessBinding.refreshUI() {
        msNeverOff.isChecked = SystemPermissionCompat.isScreenNeverOffEnabled()
        msAutoBrightness.isChecked = SystemPermissionCompat.isAutoBrightnessEnabled()
        refreshUiModeUI()

        refreshSliderUI()
        refreshBrightnessTVUI()
    }

    private fun ActivityScreenBrightnessBinding.refreshUiModeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            msUiMode.isVisible = true
            msUiMode.isChecked = SystemPermissionCompat.isDarkUIEnabled()
        }
    }

    private fun ActivityScreenBrightnessBinding.refreshSliderUI() {
        slider.isEnabled = !msAutoBrightness.isChecked
        slider.value = SystemPermissionCompat.getScreenBrightness().toFloat()
    }

    private fun ActivityScreenBrightnessBinding.refreshBrightnessTVUI() {
        tvBrightness.text = getString(R.string.percent, slider.value.toInt())
    }
}