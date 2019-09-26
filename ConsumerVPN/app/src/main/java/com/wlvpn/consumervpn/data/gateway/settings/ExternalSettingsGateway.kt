package com.wlvpn.consumervpn.data.gateway.settings

import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.gentlebreeze.vpn.sdk.model.VpnConnectionProtocolOptions
import com.wlvpn.consumervpn.data.failure.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.failure.map.ThrowableMapper
import com.wlvpn.consumervpn.data.toVpnProtocol
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.domain.gateway.ExternalSettingsGateway
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import io.reactivex.Maybe
import io.reactivex.Single

class ExternalSettingsGateway(
    private val vpnSdk: IVpnSdk
) : ExternalSettingsGateway, ThrowableMapper by NetworkThrowableMapper() {

    override fun getAvailablePorts(protocol: Protocol, scramble: Boolean): Maybe<List<Port>> =
        vpnSdk.fetchAvailableVpnPortOptions(
            protocol.toVpnProtocol(),
            VpnConnectionProtocolOptions.OPENVPN,
            scramble
        )
            .toSingle()
            .onErrorResumeNext { Single.error(mapThrowable(it)) }
            .flattenAsObservable { it }
            .map { Port(it.port) }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) {
                    Maybe.empty()
                } else {
                    Maybe.just(it)
                }
            }

}

