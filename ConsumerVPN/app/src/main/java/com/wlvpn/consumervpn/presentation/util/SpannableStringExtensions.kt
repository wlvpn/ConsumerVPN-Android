package com.wlvpn.consumervpn.presentation.util

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

fun SpannableString.setTextSpanColor(colorRes: Int, context: Context): SpannableString {
    this.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, colorRes)),
        0,
        this.length,
        0)
    return this
}