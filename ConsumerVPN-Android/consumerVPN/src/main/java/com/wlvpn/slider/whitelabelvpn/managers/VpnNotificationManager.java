package com.wlvpn.slider.whitelabelvpn.managers;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateFormat;

import com.gentlebreeze.vpn.sdk.model.VpnConnectionInfo;
import com.gentlebreeze.vpn.sdk.model.VpnDataUsage;
import com.gentlebreeze.vpn.sdk.model.VpnNotification;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.activities.MainActivity;
import com.wlvpn.slider.whitelabelvpn.receivers.SwitchServerReceiver;
import com.wlvpn.slider.whitelabelvpn.receivers.VpnConnectionReceiver;
import com.wlvpn.slider.whitelabelvpn.utilities.Bits;

import java.util.Date;

import javax.inject.Inject;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.support.v4.app.NotificationCompat.VISIBILITY_SECRET;

public class VpnNotificationManager {

    private static final String TAG = "VpnNotifications";
    private static final String VPN_NOTIFICATION_CONNECTION_CHANNEL = "VpnNotificationChannel";
    private static final String VPN_MAINTENANCE_CHANNEL = "VpnMaintenanceChannel";
    private static final String VPN_NOTIFICATION_REVOKE = "VpnNotificationRevokeChannel";
    private static final int VPN_NOTIFICATION_CONNECTION_ID = 1;
    private static final int VPN_NOTIFICATION_SWITCH_ID = 2;
    private static final int VPN_NOTIFICATION_REVOKE_ID = 3;

    private final Context context;
    private final Bitmap bitmapIconLarge;
    private final android.app.NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Inject
    VpnNotificationManager(Context context) {
        this.context = context;
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        bitmapIconLarge = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo);

        createNotificationChannel(
                VPN_MAINTENANCE_CHANNEL,
                R.string.notification_scheduled_maintenance_title,
                NotificationManagerCompat.IMPORTANCE_LOW
        );
        createNotificationChannel(
                VPN_NOTIFICATION_CONNECTION_CHANNEL,
                R.string.notification_vpn_connection_channel_title,
                NotificationManagerCompat.IMPORTANCE_LOW
        );
        createNotificationChannel(
                VPN_NOTIFICATION_REVOKE,
                R.string.notification_vpn_connection_revoke_title,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
        );
    }

    public VpnNotification getVpnNotificationConfiguration() {
        return new VpnNotification(
                getBaseConnectionNotification().build(),
                VPN_NOTIFICATION_CONNECTION_ID);
    }

    public VpnNotification getVpnRevokeNotification() {
        return new VpnNotification(getPermissionsRevokeNotification().build(),
                VPN_NOTIFICATION_REVOKE_ID);
    }

    /**
     * Display the switch server notification
     *
     * @param scheduledMaintenance date and time of the servers scheduled maintenance
     */
    public void displayMaintenance(Date scheduledMaintenance) {
        CharSequence dateString = DateFormat.format("MM-dd hh:mm:ss", scheduledMaintenance);
        String title = context.getString(R.string.notification_scheduled_maintenance_title);
        String actionSwitch = context.getString(R.string.notification_scheduled_maintenance_switch_servers);
        String content = context.getString(
                R.string.notification_scheduled_maintenance_content, dateString);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(content);

        Notification notification = new NotificationCompat.Builder(context, VPN_MAINTENANCE_CHANNEL)
                .setLocalOnly(false)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(bitmapIconLarge)
                .setVisibility(VISIBILITY_SECRET)
                .setStyle(bigTextStyle)
                .addAction(0, actionSwitch, getPendingSwitchIntent())
                .build();

        notificationManager.notify(TAG, VPN_NOTIFICATION_SWITCH_ID, notification);
    }

    /**
     * Cancel display of the switch server notification.
     */
    public void removeMaintenance() {
        notificationManager.cancel(TAG, VPN_NOTIFICATION_SWITCH_ID);
    }

    /**
     * Create a new instance of the connection notification.
     */
    private NotificationCompat.Builder getBaseConnectionNotification() {
        final String disconnect = context.getString(R.string.notification_vpn_action_disconnect);

        return new NotificationCompat.Builder(context, VPN_NOTIFICATION_CONNECTION_CHANNEL)
                .setLocalOnly(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(bitmapIconLarge)
                .setVisibility(VISIBILITY_SECRET)
                .setContentIntent(getPendingOpenAppIntent())
                .setUsesChronometer(true)
                .setShowWhen(true)
                .addAction(0, disconnect, getPendingDisconnectIntent());
    }

    private NotificationCompat.Builder getPermissionsRevokeNotification() {
        String title = context.getString(R.string.notification_vpn_connection_revoke_title);
        String content = context.getString(R.string.notification_vpn_connection_revoke_content);

        return new NotificationCompat.Builder(context, VPN_NOTIFICATION_CONNECTION_CHANNEL)
                .setContentTitle(title)
                .setContentText(content)
                .setLocalOnly(false)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(bitmapIconLarge)
                .setVisibility(VISIBILITY_SECRET)
                .setContentIntent(getPendingOpenAppIntent());
    }

    /**
     * Cancels the notification
     */
    public void cancelNotifications() {
        notificationManager.cancel(TAG, VPN_NOTIFICATION_CONNECTION_ID);
        notificationManager.cancel(TAG, VPN_NOTIFICATION_SWITCH_ID);
        notificationBuilder = null;
    }

    /**
     * Update the connection notification
     *
     * @param connectionTitleRes String res for the connection title
     * @param vpnConnectionInfo  connection info containing city and country
     */
    public void updateConnectionNotification(@StringRes int connectionTitleRes,
                                             VpnConnectionInfo vpnConnectionInfo) {
        if (notificationBuilder == null) {
            notificationBuilder = getBaseConnectionNotification();
        }

        String notificationTitle = context.getString(
                connectionTitleRes,
                vpnConnectionInfo.getCity(),
                vpnConnectionInfo.getCountry()
        );

        notificationBuilder.setContentTitle(notificationTitle)
                .setWhen(ConsumerVpnApplication.getVpnSdk()
                        .getConnectedDate()
                        .getTime());

        notificationManager.notify(
                VPN_NOTIFICATION_CONNECTION_ID,
                notificationBuilder.build()
        );
    }

    /**
     * Parse data usage and return back the notification data
     *
     * @param dataUsage data usage record containing up/down bytes
     * @return String parsed representation of the up/down
     */
    private String getNotificationContent(VpnDataUsage dataUsage) {
        return context.getString(
                R.string.notification_vpn_connecting_content,
                Bits.toSI(dataUsage.getDownBytesDiff()),
                Bits.toSI(dataUsage.getDownBytes()),
                Bits.toSI(dataUsage.getUpBytesDiff()),
                Bits.toSI(dataUsage.getUpBytes())
        );
    }


    /**
     * Create the Notification channel
     *
     * @param id         String channel id
     * @param title      String title
     * @param importance NotificationManagerCompat importance
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String id, @StringRes int title, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = context.getString(title);
            NotificationChannel channel = new NotificationChannel(id, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Pending intent to fire a disconnect vpn action
     *
     * @return PendingIntent
     */
    private PendingIntent getPendingDisconnectIntent() {
        final Intent intentDisconnect = new Intent(context, VpnConnectionReceiver.class);
        intentDisconnect.setAction(VpnConnectionReceiver.ACTION_DISCONNECT);
        return PendingIntent.getBroadcast(context, 0, intentDisconnect, FLAG_UPDATE_CURRENT);
    }

    /**
     * Pending Intent to Open App
     *
     * @return PendingIntent
     */
    private PendingIntent getPendingOpenAppIntent() {
        final Intent intentOpenApp = new Intent(context, MainActivity.class);
        intentOpenApp.setAction(Intent.ACTION_MAIN);
        intentOpenApp.addCategory(Intent.CATEGORY_LAUNCHER);
        return PendingIntent.getActivity(context, 0, intentOpenApp, FLAG_UPDATE_CURRENT);
    }

    /**
     * Pending intent to switch servers
     *
     * @return PendingIntent
     */
    private PendingIntent getPendingSwitchIntent() {
        Intent intentSwitch = new Intent(context, SwitchServerReceiver.class);
        return PendingIntent.getBroadcast(context, VPN_NOTIFICATION_SWITCH_ID, intentSwitch, 0);
    }
}
