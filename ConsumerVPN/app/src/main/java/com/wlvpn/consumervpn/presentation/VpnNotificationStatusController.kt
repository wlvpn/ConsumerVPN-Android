package com.wlvpn.consumervpn.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.os.Build
import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationChannel
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationFactory
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import io.reactivex.Completable
import timber.log.Timber

/**
 * We want notification management to survive when vpn service is alive and no activities are shown because we don't
 * have access to the VPN foreground service lifecycle which should manage these notifications.
 *
 */
class VpnNotificationStatusController(
    context: Application,
    private val vpnService: VpnService,
    private val schedulerProvider: SchedulerProvider,
    private val notificationManager: NotificationManager,
    vpnNotificationChannel: VpnNotificationChannel,
    private val vpnNotificationFactory: VpnNotificationFactory
) : ApplicationController(context) {

    private val notificationsToRemoveAtDisconnect: HashSet<Int> = hashSetOf()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vpnNotificationChannel.create()
        }
        // We want notification management to survive when vpn service
        // is alive and no activities are shown
        listenToVpnSates()
    }

    @SuppressLint("CheckResult")
    private fun listenToVpnSates() {
        //Because how controllers just die with all in it (including observables), we don't need disposable handling.
        vpnService.listenToConnectState()
            .distinctUntilChanged()
            .flatMapCompletable { vpnState ->
                when (vpnState) {

                    ConnectionState.DISCONNECTED -> {
                        notificationsToRemoveAtDisconnect.forEach { notificationManager.cancel(it) }
                        notificationsToRemoveAtDisconnect.clear()
                        Completable.complete()
                    }

                    ConnectionState.CONNECTING -> {
                        vpnNotificationFactory.createConnectingNotification().let {
                            notificationManager.notify(it.id, it.notification)
                            notificationsToRemoveAtDisconnect.add(it.id)
                        }

                        Completable.complete()
                    }

                    ConnectionState.CONNECTED -> {

                        vpnService.getConnectedServer()
                            .flatMapCompletable { connectedServer ->
                                vpnNotificationFactory
                                    .createConnectedToLocationNotification(connectedServer.location).let {
                                        notificationManager.notify(it.id, it.notification)
                                        notificationsToRemoveAtDisconnect.add(it.id)
                                    }

                                Completable.complete()
                            }
                    }

                    else -> {
                        Completable.complete()
                    }
                }
            }
            .subscribeOn(schedulerProvider.io())
            .subscribe({}) { throwable -> Timber.e(throwable) }
    }
}