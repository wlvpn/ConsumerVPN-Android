package com.wlvpn.consumervpn.domain.service.vpn

import com.wlvpn.consumervpn.domain.gateway.ExternalVpnConnectionGateway
import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.model.IpGeoLocationInfo
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.exception.AuthRequiredToConnectException
import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnNotPreparedException
import com.wlvpn.consumervpn.presentation.util.zipPair
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ExternalVpnService(
    private val externalVpnConnectionGateway: ExternalVpnConnectionGateway,
    private val authenticationService: UserAuthenticationService,
    private val settingsService: SettingsService
) : VpnService {

    override fun connect(): Completable =
        startVpnSetup()
            //Connect with Connection Request settings
            .andThen(connectWithStoredSettings())

    override fun disconnect(): Completable =
        externalVpnConnectionGateway.disconnect()
            // After disconnection refresh the location
            .andThen(
                externalVpnConnectionGateway
                    .fetchGeoInfo()
                    .ignoreElement()
            )

    override fun listenToConnectState(): Observable<ConnectionState> =
        externalVpnConnectionGateway.listenToConnectState()

    override fun getCurrentConnectionState(): Single<ConnectionState> =
        externalVpnConnectionGateway.getConnectionState()

    override fun getConnectedServer(): Single<Server> =
        externalVpnConnectionGateway.getConnectedServer()

    override fun isVpnConnected(): Single<Boolean> {
        return externalVpnConnectionGateway.isVpnConnected()
    }

    private fun startVpnSetup(): Completable =
        authenticationService.isAuthenticated()
            .flatMapCompletable { isAuthenticated ->
                if (isAuthenticated) {
                    Completable.complete()
                } else {
                    Completable.error(AuthRequiredToConnectException())
                }
            }
            .andThen(externalVpnConnectionGateway.setupVpn())
            .andThen(isVpnPrepared())


    private fun isVpnPrepared(): Completable =
        externalVpnConnectionGateway.isVpnPrepared()
            .flatMapCompletable { isPrepared ->
                if (isPrepared) {
                    Completable.complete()
                } else {
                    Completable.error(VpnNotPreparedException())
                }
            }

    private fun connectWithStoredSettings(): Completable =
        settingsService.getGeneralConnectionSettings()
            .zipPair(settingsService.getConnectionRequestSettings())
            .zipPair(authenticationService.getCredentials())
            .flatMapCompletable {
                val generalSettings = it.first.first
                val connectionSettings = it.first.second
                val credentials = it.second

                externalVpnConnectionGateway.connectWithConnectionRequestSettings(
                    generalSettings,
                    connectionSettings,
                    credentials
                )
            }

    override fun fetchGeoInfo(): Single<IpGeoLocationInfo> {
        return externalVpnConnectionGateway.fetchGeoInfo()
    }
}