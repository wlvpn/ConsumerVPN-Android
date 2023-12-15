package com.wlvpn.consumervpn.domain.gateway.notification

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import io.reactivex.Single

class AndroidNotificationPermissionGateway(private val application: Application) :
    NotificationPermissionGateway {
    override fun isPermissionGranted(): Single<Boolean> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Single.just(
                ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED)
        } else {
            Single.just(true)
        }
}