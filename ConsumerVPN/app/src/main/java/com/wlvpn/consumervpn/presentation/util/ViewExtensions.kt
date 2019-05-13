package com.wlvpn.consumervpn.presentation.util

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.WindowManager

/**
 * Will get current screen density
 *
 * @return The device screen density
 */
fun View.getDensity(): Int {
    // get screen width and height
    return context.resources
        .displayMetrics.densityDpi
}

/**
 * Will get screen size
 *
 * @return The application screen size contained in a point
 */
fun View.getScreenSize(): Point? {
    // get screen width and height
    val windowManager = context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val size = Point()
    windowManager.defaultDisplay.getSize(size)

    return size
}


/**
 * Helper to calculate screen ratio
 *
 * Use this to make better calculations on tablet offsets
 *
 * Works with density pre calculated or full screen values
 *
 * @param screenWidth  The current screen width
 * @param screenHeight The current screen height
 * @return Screen ratio
 */
fun View.calculateScreenRatio(screenWidth: Float,
                         screenHeight: Float): Float {
    return screenHeight / screenWidth
}


/**
 * Checks if the current view is visible and in the screen rectangle,
 * useful to determine when the view is on top but not visible to the user
 */
fun View.isVisible(): Boolean {

    if (!this.isShown) {
        return false
    }
    getScreenSize()?.let {
        val actualPosition = Rect()
        getGlobalVisibleRect(actualPosition)
        val screen = Rect(0, 0, it.x, it.y)
        return actualPosition.intersect(screen)
    }

    return false
}