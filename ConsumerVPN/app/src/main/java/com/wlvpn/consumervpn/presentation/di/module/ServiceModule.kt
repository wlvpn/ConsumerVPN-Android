package com.wlvpn.consumervpn.presentation.di.module

import com.wlvpn.consumervpn.domain.gateway.*
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import com.wlvpn.consumervpn.domain.service.authentication.ExternalUserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.ExternalUserAuthorizationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.servers.DefaultServersService
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.domain.service.settings.DefaultSettingsService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.ExternalVpnService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import dagger.Module
import dagger.Provides

/**
 * A module with Services dependencies.
 */
@Module
class ServiceModule {

    @Provides
    fun providesExternalUserAuthenticationService(
        credentialsRepository: CredentialsRepository,
        connectionRequestSettingsRepository: ConnectionRequestSettingsRepository,
        generalConnectionSettingsRepository: GeneralConnectionSettingsRepository,
        externalAuthenticationGateway: ExternalAuthenticationGateway,
        externalAuthorizationGateway: ExternalAuthorizationGateway,
        serversGateway: ExternalServersGateway
    ): UserAuthenticationService =
        ExternalUserAuthenticationService(
            credentialsRepository,
            connectionRequestSettingsRepository,
            generalConnectionSettingsRepository,
            externalAuthenticationGateway,
            externalAuthorizationGateway,
            serversGateway

        )

    @Provides
    fun providesExternalUserAuthorizationService(
        credentialsRepository: CredentialsRepository,
        externalAuthorizationGateway: ExternalAuthorizationGateway
    ): UserAuthorizationService =
        ExternalUserAuthorizationService(
            credentialsRepository,
            externalAuthorizationGateway
        )

    @Provides
    fun providesSdkExternalConnectionService(
        externalAuthenticationGateway: ExternalVpnConnectionGateway,
        userAuthenticationService: UserAuthenticationService,
        settingsService: SettingsService
    ): VpnService =
        ExternalVpnService(externalAuthenticationGateway, userAuthenticationService, settingsService)

    @Provides
    fun providesSettingsService(
        connectionSettingsRepository: ConnectionRequestSettingsRepository,
        generalConnectionSettingsRepository: GeneralConnectionSettingsRepository,
        externalSettingsGateway: ExternalSettingsGateway
    ): SettingsService =
        DefaultSettingsService(
            connectionSettingsRepository,
            generalConnectionSettingsRepository,
            externalSettingsGateway
        )

    @Provides
    fun providesDefaultServersService(externalServersGateway: ExternalServersGateway): ServersService =
        DefaultServersService(externalServersGateway)

}