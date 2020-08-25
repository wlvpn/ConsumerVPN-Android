package com.wlvpn.consumervpn.data.gateway.logs

import android.util.Log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import timber.log.Timber

private val fileLogger: Logger = LoggerFactory.getLogger(LogTree::class.java)

/**
 * A Timber tree for release builds
 */
class LogTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        val messageWithTag = if (tag != null) "[$tag] $message" else message
        when (priority) {
            Log.INFO -> fileLogger.info(messageWithTag)
            Log.ERROR -> fileLogger.error(messageWithTag)
        }
    }
}