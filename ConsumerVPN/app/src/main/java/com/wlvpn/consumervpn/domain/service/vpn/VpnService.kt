package com.wlvpn.consumervpn.domain.service.vpn

import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.model.IpGeoLocationInfo
import com.wlvpn.consumervpn.domain.model.Server
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface VpnService {


    fun connect(): Completable

    fun disconnect(): Completable

    fun listenToConnectState(): Observable<ConnectionState>

    fun isVpnConnected(): Single<Boolean>

    fun getCurrentConnectionState(): Single<ConnectionState>

    fun getConnectedServer(): Single<Server>

    fun fetchGeoInfo(): Single<IpGeoLocationInfo>

}