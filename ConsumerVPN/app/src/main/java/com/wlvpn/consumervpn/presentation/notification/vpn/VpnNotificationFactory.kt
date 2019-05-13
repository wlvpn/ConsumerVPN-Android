package com.wlvpn.consumervpn.presentation.notification.vpn

import android.content.Context
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.presentation.notification.NotificationChannelTemplate
import com.wlvpn.consumervpn.presentation.notification.NotificationTemplate
import com.wlvpn.consumervpn.presentation.util.isTv

class VpnNotificationFactory(
    private val context: Context,
    private val notificationChannel: NotificationChannelTemplate
) {

    fun createConnectedToLocationNotification(location: ServerLocation): NotificationTemplate {
        val title = context.getString(
            R.string.notification_vpn_connected_title,
            location.city ?: "",
            location.country
        )
        return VpnConnectionStatusNotification(
            context,
            notificationChannel.id,
            title,
            true,
            isTv(context)
        )
    }

    fun createConnectingNotification(): NotificationTemplate =
        VpnConnectionStatusNotification(
            context,
            notificationChannel.id,
            context.getString(R.string.notification_vpn_connecting),
            false,
            isTv(context)
        )

    fun createRevokedVpnNotification(): NotificationTemplate =
        RevokedVpnPermissionsNotification(
            context,
            notificationChannel.id
        )
}