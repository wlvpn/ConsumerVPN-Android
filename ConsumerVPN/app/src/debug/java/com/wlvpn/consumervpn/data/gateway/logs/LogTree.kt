package com.wlvpn.consumervpn.data.gateway.logs

import android.util.Log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import timber.log.Timber.DebugTree

private val fileLogger: Logger = LoggerFactory.getLogger(LogTree::class.java)

class LogTree : DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + ":" + element.lineNumber
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        super.log(priority, tag, message, throwable)
        val messageWithTag = if (tag != null) "[$tag] $message" else message
        when (priority) {
            Log.DEBUG -> fileLogger.debug(messageWithTag)
            Log.INFO -> fileLogger.info(messageWithTag)
            Log.WARN -> fileLogger.warn(messageWithTag)
            Log.ERROR -> fileLogger.error(messageWithTag)
            else -> {
            }
        }
    }

}