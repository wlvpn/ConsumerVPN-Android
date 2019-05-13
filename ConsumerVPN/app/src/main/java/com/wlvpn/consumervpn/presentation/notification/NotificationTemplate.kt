package com.wlvpn.consumervpn.presentation.notification

import android.app.Notification


interface NotificationTemplate {

    val id: Int

    val notification: Notification
}