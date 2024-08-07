package com.wlvpn.consumervpn.presentation.di.component

import com.wlvpn.consumervpn.data.receivers.VpnReceiver
import com.wlvpn.consumervpn.data.worker.ServersRefreshWorker
import com.wlvpn.consumervpn.data.worker.TokenRefreshWorker
import com.wlvpn.consumervpn.presentation.ConsumerApplication
import com.wlvpn.consumervpn.presentation.di.module.AppModule
import com.wlvpn.consumervpn.presentation.di.module.GatewayModule
import com.wlvpn.consumervpn.presentation.di.module.InteractorModule
import com.wlvpn.consumervpn.presentation.di.module.LicensesConfigurationModule
import com.wlvpn.consumervpn.presentation.di.module.RepositoryModule
import com.wlvpn.consumervpn.presentation.di.module.ServiceModule
import com.wlvpn.consumervpn.presentation.di.scope.PerApplication
import dagger.Component

/**
 * Parent component which lives in [ConsumerApplication].
 * Has modules with "global" dependencies needed for sub components or even singleton dependencies.
 * Part of PerApplication Scope.
 */
@PerApplication
@Component(
    modules = [AppModule::class,
        ServiceModule::class,
        InteractorModule::class,
        RepositoryModule::class,
        LicensesConfigurationModule::class,
        GatewayModule::class]
)
interface ApplicationComponent {

    /**
     * A reference to the [PresentationComponent] sub component.
     */
    val presenterComponent: PresentationComponent

    //region injectors
    fun inject(into: ConsumerApplication)

    fun inject(into: VpnReceiver)

    fun inject(into: TokenRefreshWorker)

    fun inject(into: ServersRefreshWorker)
    //endregion injectors
}