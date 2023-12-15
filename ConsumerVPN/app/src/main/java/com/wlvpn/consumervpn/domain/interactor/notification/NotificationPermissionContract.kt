package com.wlvpn.consumervpn.domain.interactor.notification

import io.reactivex.Single

interface NotificationPermissionContract {
    interface Interactor {
        fun execute(): Single<Status>
    }

    sealed class Status {
        object PermissionGranted : Status()
        object PermissionNotGranted : Status()
    }

    sealed class Event {
        object PermissionGranted : Event()
        object PermissionNotGranted : Event()
        data class Error(val message: String) : Event()
    }
}