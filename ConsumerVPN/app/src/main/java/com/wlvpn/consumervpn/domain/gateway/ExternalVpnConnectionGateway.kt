package com.wlvpn.consumervpn.domain.gateway

import com.wlvpn.consumervpn.domain.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ExternalVpnConnectionGateway {

    fun disconnect(): Completable

    fun setupVpn(): Completable

    fun isVpnConnected(): Single<Boolean>

    fun isVpnPrepared(): Single<Boolean>

    fun listenToConnectState(): Observable<ConnectionState>

    fun getConnectionState(): Single<ConnectionState>

    fun getConnectedServer(): Single<Server>

    fun connectWithConnectionRequestSettings(
        general: Settings.GeneralConnection,
        connection: Settings.ConnectionRequest,
        credentials: Credentials
    ): Completable

    fun fetchGeoInfo(): Single<IpGeoLocationInfo>
}