package com.wlvpn.consumervpn.data.gateway.authentication

import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.wlvpn.consumervpn.data.failure.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.failure.map.ThrowableMapper
import com.wlvpn.consumervpn.data.util.onErrorMapThrowable
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.domain.gateway.ExternalAuthenticationGateway
import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable

class ExternalAuthenticationGateway(
    private val vpnSdk: IVpnSdk
) : ExternalAuthenticationGateway, ThrowableMapper by NetworkThrowableMapper() {

    override fun authenticate(credentials: Credentials): Completable = Completable.defer {
        vpnSdk.loginWithUsername(credentials.username, credentials.password)
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .ignoreElement()
    }

    override fun logout(): Completable {
        return vpnSdk.logout()
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .ignoreElement()
    }

}