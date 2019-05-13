package com.wlvpn.consumervpn.presentation.notification.vpn

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.notification.NotificationTemplate

private const val VPN_REVOKED_NOTIFICATION_ID = 9933

internal class RevokedVpnPermissionsNotification(
    private val context: Context,
    private val notificationChannel: String
) : NotificationTemplate {

    override val id: Int = VPN_REVOKED_NOTIFICATION_ID

    override val notification: Notification
        get() =
            NotificationCompat.Builder(context, notificationChannel)
                .setOngoing(false)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(context.getText(R.string.notification_vpn_permission_revoked))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setCategory(Notification.CATEGORY_EVENT)
                    }
                }.build()

}