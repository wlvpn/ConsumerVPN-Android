package com.wlvpn.consumervpn.presentation.di.component

import com.wlvpn.consumervpn.data.receivers.StartupReceiver
import com.wlvpn.consumervpn.data.receivers.VpnReceiver
import com.wlvpn.consumervpn.presentation.ConsumerApplication
import com.wlvpn.consumervpn.presentation.di.module.AppModule
import com.wlvpn.consumervpn.presentation.di.module.GatewayModule
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
        RepositoryModule::class,
        GatewayModule::class]
)
interface ApplicationComponent {

    /**
     * A reference to the [PresentationComponent] sub component.
     */
    var presenterComponent: PresentationComponent

    //region injectors
    fun inject(into: ConsumerApplication)

    fun inject(into: VpnReceiver)

    fun inject(into: StartupReceiver)
    //endregion injectors
}