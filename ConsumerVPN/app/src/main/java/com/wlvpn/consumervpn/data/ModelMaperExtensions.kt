package com.wlvpn.consumervpn.data

import com.gentlebreeze.vpn.sdk.model.VpnPop
import com.gentlebreeze.vpn.sdk.model.VpnPortOptions
import com.gentlebreeze.vpn.sdk.model.VpnProtocolOptions
import com.gentlebreeze.vpn.sdk.model.VpnServer
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation

internal fun Protocol.toVpnProtocol(): VpnProtocolOptions =
        when (this) {
            Protocol.TCP -> VpnProtocolOptions.PROTOCOL_TCP
            Protocol.UDP -> VpnProtocolOptions.PROTOCOL_UDP
        }

internal fun Server.toVpnServer(): VpnServer {
    return VpnServer(
        "",
        location.city ?: "",
        host.ipAddress,
        isInMaintenance,
        scheduledMaintenance,
        capacity)

}

internal fun Port.toVpnPort(): VpnPortOptions = VpnPortOptions(portNumber)

internal fun VpnPop.toDomainServerLocation() : ServerLocation {
    return ServerLocation(city, country, countryCode)
}
