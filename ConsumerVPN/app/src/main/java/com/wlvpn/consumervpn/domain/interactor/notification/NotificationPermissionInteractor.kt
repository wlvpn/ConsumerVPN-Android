package com.wlvpn.consumervpn.domain.interactor.notification

import com.wlvpn.consumervpn.domain.gateway.notification.NotificationPermissionGateway
import io.reactivex.Single

class NotificationPermissionInteractor(
    private val notificationPermissionGateway: NotificationPermissionGateway
) :
    NotificationPermissionContract.Interactor {
    override fun execute(): Single<NotificationPermissionContract.Status> =
        notificationPermissionGateway.isPermissionGranted().map {
            if (it) {
                NotificationPermissionContract.Status.PermissionGranted
            } else {
                NotificationPermissionContract.Status.PermissionNotGranted
            }
        }
}