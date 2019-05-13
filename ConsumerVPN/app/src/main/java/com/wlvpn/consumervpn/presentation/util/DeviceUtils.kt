package com.wlvpn.consumervpn.presentation.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.wlvpn.consumervpn.BuildConfig

const val AMAZON_MANUFACTURER = "Amazon"

/**
 * Checks if application is running android TV

 * @param context application context
 * *
 * @return boolean
 */
fun isTv(context: Context): Boolean {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

/**
 * Returns the store URL depending of the device listType and brand
 *
 * @return The Store URL
 */
fun getStoreUrl(): String {
    val storeURL = if (isAmazonDevice()) BuildConfig.AMAZON_STORE_URL else BuildConfig.GOOGLE_PLAY_STORE_URL
    return storeURL + BuildConfig.APPLICATION_ID
}

/**
 *  Checks if the device is from Amazon
 *  @return a boolean
 */
fun isAmazonDevice() = AMAZON_MANUFACTURER == Build.MANUFACTURER
