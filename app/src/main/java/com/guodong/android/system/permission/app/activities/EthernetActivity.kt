package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.guodong.android.system.permission.api.SystemPermissionCompat
import com.guodong.android.system.permission.api.domain.NetworkAddress
import com.guodong.android.system.permission.api.util.isNetMask
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.OnCheckedChangeListener
import com.guodong.android.system.permission.app.OnCheckedChangeListenerWrapper
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.databinding.ActivityEthernetBinding
import com.guodong.android.system.permission.app.isErrorState
import com.guodong.android.system.permission.app.isGateway
import com.guodong.android.system.permission.app.isIP
import com.guodong.android.system.permission.app.openEthernetSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by john.wick on 2025/8/5
 */
class EthernetActivity : BaseActivity<ActivityEthernetBinding>(), OnCheckedChangeListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, EthernetActivity::class.java))
        }
    }

    private lateinit var ethernetCheckedChangeListener: OnCheckedChangeListenerWrapper

    override fun getViewBinding(): ActivityEthernetBinding {
        return ActivityEthernetBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivityEthernetBinding.initViews() {
        initTextChangeListener()

        ethernetCheckedChangeListener =
            OnCheckedChangeListenerWrapper(binding.msEthernet, this@EthernetActivity)
        binding.msEthernet.setOnCheckedChangeListener(ethernetCheckedChangeListener)

        binding.btnOpenEthernetSettings.setOnClickListener {
            openEthernetSettings()
        }

        binding.btnGetEthernet.setOnClickListener {
            lifecycleScope.launch { refreshEthernetUI() }
        }

        binding.btnSetStatic.setOnClickListener {
            val ip = binding.etEthernetIp.text.toString()
            val netmask = binding.etEthernetNetmask.text.toString()
            val gateway = binding.etEthernetGateway.text.toString()
            val dns1 = binding.etEthernetDns1.text.toString()
            val dns2 = binding.etEthernetDns2.text.toString()

            if (binding.layoutEthernetIp.isErrorState()) {
                return@setOnClickListener
            }

            if (binding.layoutEthernetNetmask.isErrorState()) {
                return@setOnClickListener
            }

            if (binding.layoutEthernetGateway.isErrorState()) {
                return@setOnClickListener
            }

            if (binding.layoutEthernetDns1.isErrorState()) {
                return@setOnClickListener
            }

            if (binding.layoutEthernetDns2.isErrorState()) {
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = SystemPermissionCompat.setEthernetStaticAddress(
                    ip,
                    netmask,
                    gateway,
                    dns1,
                    dns2
                )
                Toast.makeText(this@EthernetActivity, "设置静态地址: $result", Toast.LENGTH_SHORT)
                    .show()

                delay(1_000)

                refreshEthernetUI()
            }
        }

        binding.btnSetDhcp.setOnClickListener {
            lifecycleScope.launch {
                val result = SystemPermissionCompat.setEthernetDhcpAddress()
                Toast.makeText(this@EthernetActivity, "设置DHCP: $result", Toast.LENGTH_SHORT)
                    .show()

                delay(5_000)
                refreshEthernetUI()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { binding.refreshEthernetUI() }
    }

    override fun onCheckedChanged(
        buttonView: CompoundButton,
        isChecked: Boolean,
        fromUser: Boolean
    ) {
        if (fromUser) {
            lifecycleScope.launch { SystemPermissionCompat.enableEthernet(isChecked) }
        }

        binding.enabledEditText(isChecked)
        binding.enabledBtn(isChecked)
    }

    private fun initTextChangeListener() {
        binding.etEthernetIp.doAfterTextChanged {
            val error = if (it.isNullOrEmpty() || !it.isIP()) {
                getString(R.string.ethernet_ip_error)
            } else {
                null
            }

            binding.layoutEthernetIp.error = error
        }

        binding.etEthernetNetmask.doAfterTextChanged {
            val error = if (it.isNullOrEmpty() || !it.isNetMask()) {
                getString(R.string.ethernet_netmask_error)
            } else {
                null
            }

            binding.layoutEthernetNetmask.error = error
        }

        binding.etEthernetGateway.doAfterTextChanged {
            val ip = binding.etEthernetIp.text.toString()
            val netmask = binding.etEthernetNetmask.text.toString()
            val error = if (it.isNullOrEmpty() || !it.isGateway(ip, netmask)) {
                getString(R.string.ethernet_gateway_error)
            } else {
                null
            }

            binding.layoutEthernetGateway.error = error
        }

        binding.etEthernetDns1.doAfterTextChanged {
            val error = if (it.isNullOrEmpty() || !it.isIP()) {
                getString(R.string.ethernet_dns1_error)
            } else {
                null
            }

            binding.layoutEthernetDns1.error = error
        }

        binding.etEthernetDns2.doAfterTextChanged {
            val error = if (it.isNullOrEmpty() || !it.isIP()) {
                getString(R.string.ethernet_dns2_error)
            } else {
                null
            }

            binding.layoutEthernetDns2.error = error
        }
    }

    private suspend fun ActivityEthernetBinding.refreshEthernetUI() {
        ethernetCheckedChangeListener.setChecked(SystemPermissionCompat.isEthernetEnabled())

        val address = getEthernetNetworkAddress()
        val mode = when (address.ipAssignment) {
            NetworkAddress.IpAssignment.STATIC -> {
                enabledEditText(true)
                getString(R.string.ethernet_mode_static)
            }

            NetworkAddress.IpAssignment.DHCP -> {
                enabledEditText(false)
                getString(R.string.ethernet_mode_dhcp)
            }

            else -> {
                enabledEditText(true)
                getString(R.string.ethernet_mode_unassigned)
            }
        }

        tvEthernetMode.text = mode

        etEthernetIp.setText(address.address)
        etEthernetNetmask.setText(address.netmask)
        etEthernetGateway.setText(address.gateway)
        etEthernetDns1.setText(address.dns1)
        etEthernetDns2.setText(address.dns2)

        tvEthernetMac.text = SystemPermissionCompat.getEthernetMacAddress()
    }

    private fun ActivityEthernetBinding.enabledEditText(enable: Boolean) {
        etEthernetIp.isEnabled = enable
        etEthernetNetmask.isEnabled = enable
        etEthernetGateway.isEnabled = enable
        etEthernetDns1.isEnabled = enable
        etEthernetDns2.isEnabled = enable
    }

    private fun ActivityEthernetBinding.enabledBtn(enable: Boolean) {
        btnGetEthernet.isEnabled = enable
        btnSetStatic.isEnabled = enable
        btnSetDhcp.isEnabled = enable
    }

    private suspend fun getEthernetNetworkAddress(): NetworkAddress {
        return SystemPermissionCompat.getEthernetNetworkAddress()
    }
}