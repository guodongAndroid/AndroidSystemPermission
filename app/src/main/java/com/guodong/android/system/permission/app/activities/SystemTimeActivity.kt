package com.guodong.android.system.permission.app.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.guodong.android.system.permission.SystemPermissionCompat
import com.guodong.android.system.permission.app.BaseActivity
import com.guodong.android.system.permission.app.R
import com.guodong.android.system.permission.app.adapter.TimeZoneAdapter
import com.guodong.android.system.permission.app.databinding.ActivitySystemTimeBinding
import com.guodong.android.system.permission.app.model.TimeZoneModel
import com.guodong.android.system.permission.app.openDateSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Created by guodongAndroid on 2025/8/14
 */
class SystemTimeActivity : BaseActivity<ActivitySystemTimeBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SystemTimeActivity::class.java))
        }
    }

    private var job: Job? = null

    private val models = mutableListOf<TimeZoneModel>()

    override fun getViewBinding(): ActivitySystemTimeBinding {
        return ActivitySystemTimeBinding.inflate(LayoutInflater.from(this))
    }

    override fun ActivitySystemTimeBinding.initViews() {
        btnOpenDateSettings.setOnClickListener {
            openDateSettings()
        }

        TimeZone.getAvailableIDs().map { TimeZone.getTimeZone(it) }
            .map { TimeZoneModel(it.id, it.getDisplayName(false, TimeZone.SHORT), it.displayName) }
            .let { models.addAll(it) }
        val zoneAdapter = TimeZoneAdapter(this@SystemTimeActivity, models)
        acsTimeZones.adapter = zoneAdapter
        acsTimeZones.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                models.forEach { it.isSelected = false }
                val model = models[position]
                model.isSelected = true
                Toast.makeText(this@SystemTimeActivity, model.id, Toast.LENGTH_SHORT).show()
                zoneAdapter.notifyDataSetChanged()
                lifecycleScope.launch { SystemPermissionCompat.setTimeZone(model.id) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        btnDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getDefault()
                calendar.timeInMillis = it
                lifecycleScope.launch {
                    SystemPermissionCompat.setDate(
                        calendar[Calendar.YEAR],
                        calendar[Calendar.MONTH] + 1,
                        calendar[Calendar.DAY_OF_MONTH]
                    )
                }
            }
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        btnTime.setOnClickListener {
            val current = Calendar.getInstance()
            current.timeZone = TimeZone.getDefault()

            val timePicker = MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(current[Calendar.HOUR_OF_DAY])
                .setMinute(current[Calendar.MINUTE])
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getDefault()
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                lifecycleScope.launch {
                    SystemPermissionCompat.setTime(
                        calendar[Calendar.HOUR_OF_DAY],
                        calendar[Calendar.MINUTE],
                        calendar[Calendar.SECOND]
                    )
                }
            }
            timePicker.show(supportFragmentManager, "TimePicker")
        }

        ms24h.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch { SystemPermissionCompat.enableTimeFormat24H(isChecked) }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshUI()
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    private fun ActivitySystemTimeBinding.refreshUI() {
        job?.cancel()
        job = lifecycleScope.launch {
            ms24h.isChecked = SystemPermissionCompat.isTimeFormat24HEnabled()

            while (isActive) {
                refreshTimeZoneUI()
                refreshDateTimeUI()
                delay(1_000L)
            }
        }

        val defaultTimeZoneId = TimeZone.getDefault().id
        val indexOfFirst = models.indexOfFirst { defaultTimeZoneId == it.id }
        acsTimeZones.setSelection(indexOfFirst)
    }

    private fun ActivitySystemTimeBinding.refreshTimeZoneUI() {
        val timeZone = TimeZone.getDefault()
        tvTimeZone.text = getString(
            R.string.current_time_zone,
            "${timeZone.id} ${
                timeZone.getDisplayName(
                    false,
                    TimeZone.SHORT
                )
            }/${timeZone.displayName}"
        )
    }

    private fun ActivitySystemTimeBinding.refreshDateTimeUI() {
        val dateTime = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        tvDateTime.text = getString(R.string.current_date_time, dateTime)
    }
}