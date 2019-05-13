package com.wlvpn.consumervpn.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson

open class BaseSharedPreferencesRepository(
        protected val baseKeyId: String,
        protected val privateSharedPreferences: SharedPreferences,
        protected val gson: Gson
)