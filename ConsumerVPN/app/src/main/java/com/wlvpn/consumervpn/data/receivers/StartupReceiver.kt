package com.wlvpn.consumervpn.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.splash.SplashActivity
import javax.inject.Inject

class StartupReceiver : BroadcastReceiver() {

    @Inject
    lateinit var service: SettingsService

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            context?.let {
                Injector.INSTANCE.applicationComponent?.inject(this)

                val settings =
                    service.getGeneralConnectionSettings()
                        .blockingGet()

                if (settings.launchOnStartup
                    && Intent.ACTION_BOOT_COMPLETED == intent.action
                ) {
                    val activityIntent = Intent(context, SplashActivity::class.java)
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(activityIntent)
                }
            }
        }
    }

}
