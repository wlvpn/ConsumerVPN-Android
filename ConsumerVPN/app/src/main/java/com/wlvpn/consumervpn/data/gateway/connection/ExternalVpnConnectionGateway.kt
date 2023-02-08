package com.wlvpn.consumervpn.data.gateway.connection

import com.gentlebreeze.vpn.core.configuration.ApiAuthMode
import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.gentlebreeze.vpn.sdk.model.VpnConnectionConfiguration
import com.gentlebreeze.vpn.sdk.model.VpnConnectionProtocolOptions
import com.gentlebreeze.vpn.sdk.model.VpnNotification
import com.gentlebreeze.vpn.sdk.model.VpnPop
import com.gentlebreeze.vpn.sdk.model.VpnState
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.BuildConfig.IKE_REMOTE_ID
import com.wlvpn.consumervpn.data.failure.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.failure.map.ThrowableMapper
import com.wlvpn.consumervpn.data.gateway.connection.failure.NoServersFoundForSelectionFailure
import com.wlvpn.consumervpn.data.gateway.connection.failure.ServerNotSelectedToVpnFailure
import com.wlvpn.consumervpn.data.model.CityAndCountryServerLocation
import com.wlvpn.consumervpn.data.model.CountryServerLocation
import com.wlvpn.consumervpn.data.toVpnConnectionProtocol
import com.wlvpn.consumervpn.data.toVpnPort
import com.wlvpn.consumervpn.data.toVpnProtocol
import com.wlvpn.consumervpn.data.toVpnServer
import com.wlvpn.consumervpn.data.util.onErrorMapThrowable
import com.wlvpn.consumervpn.data.util.toObservable
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.domain.gateway.ExternalVpnConnectionGateway
import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.model.IpGeoLocationInfo
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerHost
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

class ExternalVpnConnectionGateway(
    private val vpnSdk: IVpnSdk,
    private val vpnNotificationFactory: VpnNotificationFactory
) : ExternalVpnConnectionGateway, ThrowableMapper by NetworkThrowableMapper() {

    //Sdk needs notifications to start foreground service and when vpn is revoked
    //This is the initial notification to be used when a connection starts
    private val notificationConfiguration: VpnNotification
        get() {
            val notification = vpnNotificationFactory.createConnectingNotification()
            return VpnNotification(notification.notification, notification.id)
        }

    //This is called when VPN is revoked by the user (or another app)
    private val revokedVpnNotification: VpnNotification
        get() {
            val notification = vpnNotificationFactory.createRevokedVpnNotification()
            return VpnNotification(notification.notification, notification.id)
        }

    override fun disconnect(): Completable = vpnSdk.disconnect()
        .toSingle()
        .onErrorMapThrowable { mapThrowable(it) }
        .ignoreElement()

    override fun setupVpn(): Completable {
        return fetchGeoInfo()
            .onErrorMapThrowable { mapThrowable(it) }
            .ignoreElement()
    }

    override fun isVpnConnected(): Single<Boolean> = Single.defer {
        Single.just(vpnSdk.isConnected())
    }.onErrorResumeNext {
        Single.error(mapThrowable(it))
    }

    override fun isVpnPrepared(): Single<Boolean> = Single.defer {
        Single.just(vpnSdk.isVpnServicePrepared())
    }.onErrorResumeNext {
        Single.error(mapThrowable(it))
    }

    override fun listenToConnectState(): Observable<ConnectionState> =
        vpnSdk.listenToConnectState().toObservable()
            .flatMap {
                    Observable.just(vpnStateToDomainState(it.connectionState))
                }.onErrorMapThrowable { mapThrowable(it) }

    override fun getConnectionState(): Single<ConnectionState> {
        return Single.just(vpnSdk.getConnectionState())
            .flatMap {
                Single.just(vpnStateToDomainState(it))
            }.onErrorResumeNext {
                Single.error(mapThrowable(it))
            }
    }

    override fun getConnectedServer(): Single<Server> {
        return Single.just(vpnSdk.getConnectionInfo())
            .flatMap { connectionInfo ->
                val serverHost = ServerHost(
                    connectionInfo.name,
                    connectionInfo.ipAddress
                )

                val serverLocation = CityAndCountryServerLocation(
                    connectionInfo.city,
                    connectionInfo.country,
                    connectionInfo.countryCode
                )
                Single.just(Server(serverHost, serverLocation))
            }
            .onErrorMapThrowable { mapThrowable(it) }
    }

    override fun connectWithConnectionRequestSettings(
        general: Settings.GeneralConnection,
        connectionRequest: Settings.ConnectionRequest,
        credentials: Credentials
    ): Completable = Completable.defer {
        val connectionConfig = buildVpnConnectionConfig(credentials, general)

        //TODO we need to move this logic to domain, for now this can live here
        connectionRequest.server?.let {
            connectWithServer(it, connectionConfig)
        } ?: run {
            when (connectionRequest.location) {
                is CityAndCountryServerLocation, is CountryServerLocation ->
                    connectWithLocation(connectionRequest.location, connectionConfig)
                else -> connectToFastestServer(connectionConfig)
            }
        }
    }.onErrorResumeNext {
        Completable.error(mapThrowable(it))
    }

    private fun connectToFastestServer(connectionConfig: VpnConnectionConfiguration): Completable =
        vpnSdk.connectToNearest(
            notificationConfiguration,
            revokedVpnNotification,
            connectionConfig
        )
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .ignoreElement()

    private fun connectWithServer(
        server: Server?,
        connectionConfig: VpnConnectionConfiguration
    ): Completable = Completable.defer {
        if (server != null) {
            vpnSdk.connect(
                server.toVpnServer(),
                notificationConfiguration,
                connectionConfig,
                revokedVpnNotification
            ).toSingle()
                .onErrorMapThrowable { mapThrowable(it) }
                .ignoreElement()
        } else {
            Completable.error(ServerNotSelectedToVpnFailure())
        }
    }

    private fun connectWithLocation(
        location: ServerLocation,
        connectionConfig: VpnConnectionConfiguration
    ): Completable = Completable.defer {

        //Get city pop and connect to it, otherwise, default with country
        when (location) {
            is CityAndCountryServerLocation -> {
                vpnSdk.fetchPopByCountryCodeAndCity(location.countryCode, location.city)
                    .toSingle()
                    .flatMapCompletable {
                        connectWitVpnPop(it, connectionConfig)
                    }
            }
            is CountryServerLocation -> {
                vpnSdk.fetchPopsByCountryQuery(location.country)
                    .toSingle()
                    .flattenAsObservable { it }
                    .firstElement()
                    .switchIfEmpty(Maybe.error(NoServersFoundForSelectionFailure()))
                    .flatMapCompletable {
                        connectWitVpnPop(it, connectionConfig)
                    }
            }
            else -> connectToFastestServer(connectionConfig)
        }.onErrorMapThrowable { mapThrowable(it) }
    }

    private fun connectWitVpnPop(
        vpnPop: VpnPop,
        vpnConnectionConfiguration: VpnConnectionConfiguration
    ): Completable = vpnSdk.connectToNearest(
        vpnPop,
        notificationConfiguration,
        revokedVpnNotification,
        vpnConnectionConfiguration
    )
        .toSingle()
        .onErrorMapThrowable { mapThrowable(it) }
        .ignoreElement()

    private fun buildVpnConnectionConfig(
        credentials: Credentials,
        general: Settings.GeneralConnection
    ): VpnConnectionConfiguration =
        VpnConnectionConfiguration.Builder(
            vpnSdk.getAuthInfo().vpnAuthUsername ?: credentials.username,
            vpnSdk.getAuthInfo().vpnAuthPassword ?: credentials.password
        )
            .scrambleOn(general.scramble)
            .reconnectOn(general.autoReconnect)
            .remoteId(IKE_REMOTE_ID)
            .apply {
                // TODO: implement port settings to VPN protocol selection relationship, currently
                // the settings repository only consider one port option ignoring the available
                // options for each VPN protocol
                when(general.vpnProtocol.toVpnConnectionProtocol()){
                    VpnConnectionProtocolOptions.OPENVPN -> {
                        port(general.port.toVpnPort())
                    }
                    VpnConnectionProtocolOptions.WIREGUARD -> {
                        apiAuthMode(ApiAuthMode.BEARER_TOKEN_AUTH)
                    }
                    else -> {
                        // No op
                    }
                }

            }
            .vpnProtocol(general.protocol.toVpnProtocol())
            .shouldOverrideMobileMtu(false)
            .connectionProtocol(general.vpnProtocol.toVpnConnectionProtocol())
            .debugLevel(if (BuildConfig.DEBUG) 5 else 0)
            .build()

    private fun vpnStateToDomainState(state: Int): ConnectionState =
        when (state) {
            VpnState.CONNECTED -> {
                ConnectionState.CONNECTED
            }

            VpnState.CONNECTING -> {
                ConnectionState.CONNECTING
            }

            VpnState.DISCONNECTED -> {
                ConnectionState.DISCONNECTED
            }

            VpnState.DISCONNECTED_ERROR -> {
                ConnectionState.DISCONNECTED_ERROR
            }

            else -> {
                Timber.e("An unknown state received: $state")
                ConnectionState.UNKNOWN
            }
        }

    override fun fetchGeoInfo(): Single<IpGeoLocationInfo> {
        return vpnSdk.fetchGeoInfo()
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMap { vpnGeoData ->
                Single.just(
                    IpGeoLocationInfo(
                        vpnGeoData.geoCity,
                        vpnGeoData.geoCountryCode,
                        vpnGeoData.geoIp!!,
                        vpnGeoData.geoLatitude,
                        vpnGeoData.geoLongitude
                    )
                )
            }
    }
}