package com.wlvpn.consumervpn.presentation.di.module

import android.content.res.Resources
import com.netprotect.licenses.implementation.input.LicensesInputLocator
import com.wlvpn.consumervpn.presentation.di.provider.LocalLicensesInputLocator
import dagger.Module
import dagger.Provides

@Module
class LicensesConfigurationModule {

    @Provides
    fun providesLicensesInputLocator(resources: Resources): LicensesInputLocator {
        return LocalLicensesInputLocator(resources)
    }

}
