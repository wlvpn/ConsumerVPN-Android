package com.wlvpn.consumervpn.presentation.util

/**
 * A class to keep track of the app startup
 */
interface StartupStatus {

    var isFreshStart: Boolean

    /**
     * Resets [isFreshStart] to the default value
     */
    fun reset()
}