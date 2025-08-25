package com.guodong.android.system.permission.app

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by guodongAndroid on 2025/8/5
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService<InputMethodManager>()!!
    clearFocus()
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextInputLayout.isErrorState(): Boolean {
    return isErrorEnabled && !error.isNullOrEmpty()
}