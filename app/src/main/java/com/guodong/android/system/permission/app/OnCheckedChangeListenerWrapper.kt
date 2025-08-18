package com.guodong.android.system.permission.app

import android.widget.CompoundButton

/**
 * Created by guodongAndroid on 2025/8/19
 */
fun interface OnCheckedChangeListener {
    fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean, fromUser: Boolean)
}

class OnCheckedChangeListenerWrapper(
    private val buttonView: CompoundButton,
    private val listener: OnCheckedChangeListener
) : CompoundButton.OnCheckedChangeListener {

    private var fromUser: Boolean = true

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        listener.onCheckedChanged(buttonView, isChecked, fromUser)
    }

    fun setChecked(checked: Boolean) {
        fromUser = false
        buttonView.isChecked = checked
        fromUser = true
    }
}