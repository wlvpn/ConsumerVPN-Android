package com.wlvpn.consumervpn.presentation.notification.vpn

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.data.receivers.ACTION_DISCONNECT
import com.wlvpn.consumervpn.data.receivers.VpnReceiver
import com.wlvpn.consumervpn.presentation.features.home.HomeActivity
import com.wlvpn.consumervpn.presentation.notification.NotificationTemplate
import java.util.Date

private const val VPN_CONNECTION_NOTIFICATION_ID = 3399

internal class VpnConnectionStatusNotification(
    private val context: Context,
    private val notificationChannel: String,
    private val title: String,
    private val showTimer: Boolean,
    private val isTv: Boolean
) : NotificationTemplate {

    override val id: Int = VPN_CONNECTION_NOTIFICATION_ID

    override val notification: Notification
    get() =
        NotificationCompat.Builder(context, notificationChannel)
            .setLocalOnly(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification))
            .setContentIntent(pendingOpenAppIntent)
            .setUsesChronometer(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .addAction(0, context.getString(R.string.notification_vpn_action_disconnect), pendingDisconnectIntent)
            .setContentTitle(title)
            .setShowWhen(true)
            .setWhen(if (showTimer) Date().time else 0)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (isTv) {
                        setCategory(Notification.CATEGORY_RECOMMENDATION)
                    } else {
                        setCategory(Notification.CATEGORY_SERVICE)
                    }
                }
            }.build()

    /**
     * Pending intent to fire a disconnect vpn action
     *
     * @return PendingIntent
     */
    private val pendingDisconnectIntent: PendingIntent
        get() {
            val intentDisconnect = Intent(context, VpnReceiver::class.java)
            intentDisconnect.action = ACTION_DISCONNECT
            return PendingIntent.getBroadcast(context, 0, intentDisconnect, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    /**
     * Pending Intent to Open App
     *
     * @return PendingIntent
     */
    private val pendingOpenAppIntent: PendingIntent
        get() {
            val intentOpenApp = Intent(context, HomeActivity::class.java)
            intentOpenApp.action = Intent.ACTION_MAIN
            intentOpenApp.addCategory(Intent.CATEGORY_LAUNCHER)

            return PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
        }

}