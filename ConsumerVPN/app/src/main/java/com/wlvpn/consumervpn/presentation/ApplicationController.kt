package com.wlvpn.consumervpn.presentation

import android.app.Application

/**
 * Parent class to any Application controller, these controllers are ment to be used in application lifecycle to do work
 * when no activities are shown.
 *
 * This should be avoided, this a last resort. There are better ways to handle background work.
 *
 * As a warning is, this object will die without warning along with the application process.
 */
open class ApplicationController(protected val applicationContext: Application)