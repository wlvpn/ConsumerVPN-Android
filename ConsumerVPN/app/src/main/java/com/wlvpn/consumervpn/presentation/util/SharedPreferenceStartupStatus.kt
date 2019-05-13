package com.wlvpn.consumervpn.presentation.util

import android.content.SharedPreferences

class SharedPreferenceStartupStatus(
    private val baseKeyId: String,
    private val sharedPreferences: SharedPreferences
) : StartupStatus {

    private val startupStatusKey get() = "$baseKeyId.IS_FIRST_RUN_KEY"

    override var isFreshStart: Boolean
        get() = sharedPreferences.getBoolean(startupStatusKey, false)
        set(value) = sharedPreferences.edit().putBoolean(startupStatusKey, value).apply()

    override fun reset() = sharedPreferences.edit().remove(startupStatusKey).apply()

}