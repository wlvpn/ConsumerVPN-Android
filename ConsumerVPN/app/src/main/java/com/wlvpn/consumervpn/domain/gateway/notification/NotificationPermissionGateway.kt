package com.wlvpn.consumervpn.domain.gateway.notification

import io.reactivex.Single

interface NotificationPermissionGateway {
    fun isPermissionGranted(): Single<Boolean>
}