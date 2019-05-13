package com.wlvpn.consumervpn.data.gateway.connection

import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.gentlebreeze.vpn.sdk.model.*
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.data.exception.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.exception.map.ThrowableMapper
import com.wlvpn.consumervpn.data.gateway.connection.exception.NoServersFoundForSelectionException
import com.wlvpn.consumervpn.data.gateway.connection.exception.ServerNotSelectedToVpnException
import com.wlvpn.consumervpn.data.toVpnPort
import com.wlvpn.consumervpn.data.toVpnProtocol
import com.wlvpn.consumervpn.data.toVpnServer
import com.wlvpn.consumervpn.data.util.onErrorMapThrowable
import com.wlvpn.consumervpn.data.util.toObservable
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.domain.gateway.ExternalVpnConnectionGateway
import com.wlvpn.consumervpn.domain.model.*
import com.wlvpn.consumervpn.domain.model.Settings.ConnectionRequest.ConnectOption
import com.wlvpn.consumervpn.domain.service.settings.exception.LocationNotSelectedException
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationFactory
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

private const val AUTH_FAILURE_DESCRIPTION = "Authentication failure"

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
                //TODO HACK, this forces to remove notification, the SDK should fix this ASAP
                if (vpnSdk.getConnectionDescription() == AUTH_FAILURE_DESCRIPTION) {
                    vpnSdk.disconnect()
                            .toSingle()
                            .ignoreElement()
                            .andThen(Observable.just(vpnStateToDomainState(it.connectionState)))
                } else {
                    Observable.just(vpnStateToDomainState(it.connectionState))
                }
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

                val serverLocation = ServerLocation(
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
        connection: Settings.ConnectionRequest,
        credentials: Credentials
    ): Completable = Completable.defer {
        val connectionConfig = buildVpnConnectionConfig(credentials, general)

        //TODO we need to move this logic to domain, for now this can live here
        when (connection.connectionOption) {
            ConnectOption.FASTEST_SERVER ->
                connectToFastestServer(connectionConfig)
            ConnectOption.FASTEST_IN_LOCATION ->
                connectWithLocation(connection.location, connectionConfig)
            ConnectOption.WITH_SERVER ->
                connectWithServer(connection.server, connectionConfig)
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
            Completable.error(ServerNotSelectedToVpnException())
        }
    }

    private fun connectWithLocation(
        location: ServerLocation?,
        connectionConfig: VpnConnectionConfiguration
    ): Completable = Completable.defer {

        if (location == null) {
            Completable.error(LocationNotSelectedException())
        } else {
            val city = location.city

            //Get city pop and connect to it, otherwise, default with country
            if (city != null) {
                vpnSdk.fetchPopByCountryCodeAndCity(location.countryCode, city)
                    .toSingle()
                    .flatMapCompletable {
                        connectWitVpnPop(it, connectionConfig)
                    }
            } else {
                vpnSdk.fetchPopsByCountryQuery(location.country)
                    .toSingle()
                    .flattenAsObservable { it }
                    .firstElement()
                    .switchIfEmpty(Maybe.error(NoServersFoundForSelectionException()))
                    .flatMapCompletable {
                        connectWitVpnPop(it, connectionConfig)
                    }
            }.onErrorMapThrowable { mapThrowable(it) }
        }
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
            .reconnetOn(general.autoReconnect)
            .port(general.port.toVpnPort())
            .vpnProtocol(general.protocol.toVpnProtocol())
            .shouldOverrideMobileMtu(false)
            .connectionProtocol(VpnConnectionProtocolOptions.OPENVPN)
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
                        vpnGeoData.geoIp,
                        vpnGeoData.geoLatitude,
                        vpnGeoData.geoLongitude
                    )
                )
            }
    }
}



