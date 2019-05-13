package com.wlvpn.consumervpn.presentation.notification

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

abstract class NotificationChannelTemplate(private val notificationManager: NotificationManager) {

    abstract val id: String

    abstract val title: String

    @TargetApi(Build.VERSION_CODES.O)
    fun create() {
        NotificationChannel(
            id,
            title,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
            notificationManager.createNotificationChannel(this)
        }
    }
}