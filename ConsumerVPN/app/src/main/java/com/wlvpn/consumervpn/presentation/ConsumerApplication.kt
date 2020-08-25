package com.wlvpn.consumervpn.presentation

import android.app.Application
import com.evernote.android.job.JobManager
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.netprotect.licenses.implementation.input.LicensesInputLocator
import com.netprotect.licenses.implementation.install.Licenses
import com.squareup.leakcanary.LeakCanary
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.data.gateway.logs.LogTree
import com.wlvpn.consumervpn.data.job.ConsumerJobsCreator
import com.wlvpn.consumervpn.presentation.di.Injector
import timber.log.Timber
import javax.inject.Inject

class ConsumerApplication : Application() {

    @Inject
    lateinit var jobCreator: ConsumerJobsCreator

    @Inject
    lateinit var licensesInputLocator: LicensesInputLocator

    //We just inject it in order to dagger create the instance
    @Inject
    lateinit var vpnNotificationStatusController: VpnNotificationStatusController

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {
            FLog.setMinimumLoggingLevel(FLog.VERBOSE)
        }

        Timber.plant(LogTree())

        Fresco.initialize(this)

        Injector.INSTANCE.initAppComponent(this)
        Injector.INSTANCE.applicationComponent?.inject(this)

        JobManager.create(this).addJobCreator(jobCreator)

        Licenses.Install.INSTANCE.init(this,licensesInputLocator);

    }

}