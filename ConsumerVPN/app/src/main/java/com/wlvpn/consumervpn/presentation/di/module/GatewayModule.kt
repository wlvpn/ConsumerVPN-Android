package com.wlvpn.consumervpn.presentation.di.module

import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.wlvpn.consumervpn.data.gateway.authentication.ExternalAuthenticationGateway
import com.wlvpn.consumervpn.data.gateway.authorization.ExternalAuthorizationGateway
import com.wlvpn.consumervpn.data.gateway.connection.ExternalVpnConnectionGateway
import com.wlvpn.consumervpn.data.gateway.servers.ExternalServersGateway
import com.wlvpn.consumervpn.data.gateway.settings.ExternalSettingsGateway
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationFactory
import dagger.Module
import dagger.Provides

@Module
class GatewayModule {

    @Provides
    fun providesExternalAuthenticationGateway(
        vpnSdk: IVpnSdk
    ): com.wlvpn.consumervpn.domain.gateway.ExternalAuthenticationGateway =
        ExternalAuthenticationGateway(vpnSdk)

    @Provides
    fun providesExternalAuthorizationGateway(
        vpnSdk: IVpnSdk
    ): com.wlvpn.consumervpn.domain.gateway.ExternalAuthorizationGateway =
        ExternalAuthorizationGateway(vpnSdk)

    @Provides
    fun providesExternalSettingsGateway(vpnSdk: IVpnSdk): com.wlvpn.consumervpn.domain.gateway.ExternalSettingsGateway =
        ExternalSettingsGateway(vpnSdk)

    @Provides
    fun providesSdkConnectionService(
        vpnSdk: IVpnSdk,
        vpnNotificationFactory: VpnNotificationFactory
    ): com.wlvpn.consumervpn.domain.gateway.ExternalVpnConnectionGateway = ExternalVpnConnectionGateway(
        vpnSdk,
        vpnNotificationFactory
    )

    @Provides
    fun providesSdkExternalServersGateway(vpnSdk: IVpnSdk): com.wlvpn.consumervpn.domain.gateway.ExternalServersGateway =
        ExternalServersGateway(vpnSdk)

}