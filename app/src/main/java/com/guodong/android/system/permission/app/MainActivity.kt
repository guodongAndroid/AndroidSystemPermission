package com.guodong.android.system.permission.app

import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.activities.EthernetActivity
import com.guodong.android.system.permission.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun initViews() {
        binding.tvAppVersion.text = versionName

        binding.tvSdkVersion.text =
            getString(R.string.sdk_version, SystemPermissionCompat.getVersion())

        lifecycleScope.launch {
            binding.tvFirmwareVersion.text =
                getString(R.string.firmware_version, SystemPermissionCompat.getFirmwareVersion())
        }

        binding.btnEthernet.setOnClickListener { EthernetActivity.start(this) }
    }
}