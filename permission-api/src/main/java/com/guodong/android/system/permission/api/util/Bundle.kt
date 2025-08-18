package com.guodong.android.system.permission.api.util

import android.os.Bundle
import android.os.Parcelable

/**
 * Created by john.wick on 2025/5/27
 */
@Suppress("FunctionName")
fun Bundle.ToString(): String {
    val b = StringBuilder(128)
    b.append("Bundle[{")
    ToShortString(b)
    b.append("}]")
    return b.toString()
}

@Suppress("FunctionName")
fun Bundle.ToShortString(b: StringBuilder) {
    var first = true
    for (key in this.keySet()) {
        if (!first) {
            b.append(", ")
        }
        b.append(key).append('=')

        @Suppress("DEPRECATION")
        val value = this[key]
        if (value is IntArray) {
            b.append(value.contentToString())
        } else if (value is ByteArray) {
            b.append(value.contentToString())
        } else if (value is BooleanArray) {
            b.append(value.contentToString())
        } else if (value is ShortArray) {
            b.append(value.contentToString())
        } else if (value is LongArray) {
            b.append(value.contentToString())
        } else if (value is FloatArray) {
            b.append(value.contentToString())
        } else if (value is DoubleArray) {
            b.append(value.contentToString())
        } else if (value is Array<*> && value.isArrayOf<String>()) {
            b.append(value.contentToString())
        } else if (value is Array<*> && value.isArrayOf<CharSequence>()) {
            b.append(value.contentToString())
        } else if (value is Array<*> && value.isArrayOf<Parcelable>()) {
            b.append(value.contentToString())
        } else if (value is Bundle) {
            b.append(value.ToString())
        } else {
            b.append(value)
        }
        first = false
    }
}