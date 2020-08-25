package com.wlvpn.consumervpn.presentation.di.module

import android.app.Application
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.interactor.logs.GetApplicationLogsContract
import com.wlvpn.consumervpn.domain.interactor.logs.SendCommentsContract
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.bus.Event
import com.wlvpn.consumervpn.presentation.bus.SinglePipelineBus
import com.wlvpn.consumervpn.presentation.di.scope.PerPresentation
import com.wlvpn.consumervpn.presentation.features.about.AboutContract
import com.wlvpn.consumervpn.presentation.features.about.AboutPresenter
import com.wlvpn.consumervpn.presentation.features.home.HomeContract
import com.wlvpn.consumervpn.presentation.features.home.HomePresenter
import com.wlvpn.consumervpn.presentation.features.home.connection.ConnectionContract
import com.wlvpn.consumervpn.presentation.features.home.connection.ConnectionPresenter
import com.wlvpn.consumervpn.presentation.features.home.servers.ServersContract
import com.wlvpn.consumervpn.presentation.features.home.servers.ServersPresenter
import com.wlvpn.consumervpn.presentation.features.login.LoginContract
import com.wlvpn.consumervpn.presentation.features.login.LoginPresenter
import com.wlvpn.consumervpn.presentation.features.settings.SettingsContract
import com.wlvpn.consumervpn.presentation.features.settings.SettingsPresenter
import com.wlvpn.consumervpn.presentation.features.splash.SplashContract
import com.wlvpn.consumervpn.presentation.features.splash.SplashPresenter
import com.wlvpn.consumervpn.presentation.features.support.ContactSupportContract
import com.wlvpn.consumervpn.presentation.features.support.ContactSupportPresenter
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.StartupStatus
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * A module with presenters dependencies.
 *
 * Always use @PerPresentation, otherwise the presenter will not survive.
 */
@Module
class PresenterModule {

    @Provides
    @PerPresentation
    fun providesSplashContractPresenter(
        schedulerProvider: SchedulerProvider,
        userAuthenticationService: UserAuthenticationService,
        startupStatus: StartupStatus
    ): SplashContract.Presenter =
        SplashPresenter(schedulerProvider, userAuthenticationService, startupStatus)

    @Provides
    @PerPresentation
    fun providesLoginPresenter(
        schedulerProvider: SchedulerProvider,
        userAuthenticationService: UserAuthenticationService
    ): LoginContract.Presenter =
        LoginPresenter(userAuthenticationService, schedulerProvider)

    @Provides
    @PerPresentation
    fun providesHomePresenter(
        schedulerProvider: SchedulerProvider,
        userAuthenticationService: UserAuthenticationService,
        userAuthorizationService: UserAuthorizationService,
        serversService: ServersService,
        settingsService: SettingsService,
        @Named(CONNECT_BUS_PROPERTY) connectionBus: SinglePipelineBus<Event.ConnectionRequestEvent>,
        startupStatus: StartupStatus
    ): HomeContract.Presenter = HomePresenter(
        userAuthenticationService,
        userAuthorizationService,
        serversService,
        schedulerProvider,
        settingsService,
        connectionBus,
        startupStatus
    )

    @Provides
    @PerPresentation
    fun providesConnectionPresenter(
        service: VpnService,
        schedulerProvider: SchedulerProvider,
        settingsService: SettingsService,
        @Named(CONNECT_BUS_PROPERTY) connectionBus: SinglePipelineBus<Event.ConnectionRequestEvent>
    ): ConnectionContract.Presenter =
        ConnectionPresenter(service, schedulerProvider, settingsService, connectionBus)

    @Provides
    @PerPresentation
    fun providesServersPresenter(
        serversService: ServersService,
        settingsService: SettingsService,
        vpnService: VpnService,
        @Named(CONNECT_BUS_PROPERTY) connectionEventBus: SinglePipelineBus<Event.ConnectionRequestEvent>,
        schedulerProvider: SchedulerProvider
    ): ServersContract.Presenter =
        ServersPresenter(
            serversService,
            settingsService,
            vpnService,
            connectionEventBus,
            schedulerProvider
        )

    @Provides
    @PerPresentation
    fun providesSettingsPresenter(
        settingsService: SettingsService,
        vpnService: VpnService,
        authenticationService: UserAuthenticationService,
        schedulerProvider: SchedulerProvider
    ): SettingsContract.Presenter =
        SettingsPresenter(settingsService, vpnService, authenticationService, schedulerProvider)


    @Provides
    @PerPresentation
    fun providesAboutPresenter(): AboutContract.Presenter =
        AboutPresenter()

    @Provides
    @PerPresentation
    fun providesContactSupportPresenter(
        application: Application,
        schedulerProvider: SchedulerProvider,
        getApplicationLogsInteractor: GetApplicationLogsContract.Interactor,
        sendCommentsInteractor: SendCommentsContract.Interactor

    ): ContactSupportContract.Presenter =
        ContactSupportPresenter(
            getApplicationLogsInteractor,
            sendCommentsInteractor,
            application.getString(R.string.support_web_address),
            schedulerProvider
        )
}