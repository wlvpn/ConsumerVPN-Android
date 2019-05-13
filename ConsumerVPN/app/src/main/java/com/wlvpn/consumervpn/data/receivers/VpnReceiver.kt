package com.wlvpn.consumervpn.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.di.Injector
import timber.log.Timber
import javax.inject.Inject

const val ACTION_DISCONNECT = "com.wlvpn.consumervpn.presentation.ACTION_DISCONNECT"

//TODO check responsibilities of this receiver
class VpnReceiver : BroadcastReceiver() {

    @Inject
    lateinit var service: VpnService

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action != null) {
            Injector.INSTANCE.applicationComponent?.inject(this)

            when (intent.action) {
                ACTION_DISCONNECT -> {
                    service.disconnect()
                        .subscribe({}, { throwable ->
                            Timber.e(throwable, "Error while disconnecting")
                        })
                }

                else -> {
                }
            }
        }
    }
}
