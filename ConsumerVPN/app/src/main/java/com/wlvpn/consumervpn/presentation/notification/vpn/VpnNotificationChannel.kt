package com.wlvpn.consumervpn.presentation.notification.vpn

import android.app.NotificationManager
import com.wlvpn.consumervpn.presentation.notification.NotificationChannelTemplate


private const val VPN_NOTIFICATION_CONNECTION_CHANNEL = "VpnNotificationChannel"

class VpnNotificationChannel(
    override val title: String,
    notificationManager: NotificationManager
) : NotificationChannelTemplate(notificationManager) {

    override val id: String = VPN_NOTIFICATION_CONNECTION_CHANNEL

}