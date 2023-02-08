package com.wlvpn.consumervpn.data

import com.gentlebreeze.vpn.sdk.model.VpnConnectionProtocolOptions
import com.gentlebreeze.vpn.sdk.model.VpnPop
import com.gentlebreeze.vpn.sdk.model.VpnPortOptions
import com.gentlebreeze.vpn.sdk.model.VpnProtocolOptions
import com.gentlebreeze.vpn.sdk.model.VpnServer
import com.wlvpn.consumervpn.data.model.CityAndCountryServerLocation
import com.wlvpn.consumervpn.data.model.CountryServerLocation
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.model.VpnProtocol
import com.wlvpn.consumervpn.domain.model.VpnProtocol.IKEV2
import com.wlvpn.consumervpn.domain.model.VpnProtocol.OPENVPN
import com.wlvpn.consumervpn.domain.model.VpnProtocol.WIREGUARD

internal fun Protocol.toVpnProtocol(): VpnProtocolOptions =
        when (this) {
            Protocol.TCP -> VpnProtocolOptions.PROTOCOL_TCP
            Protocol.UDP -> VpnProtocolOptions.PROTOCOL_UDP
        }

internal fun Server.toVpnServer(): VpnServer {
    return VpnServer(
        "",
        if (location is CityAndCountryServerLocation) location.city else "",
        host.ipAddress,
        isInMaintenance,
        scheduledMaintenance,
        capacity)

}

internal fun Port.toVpnPort(): VpnPortOptions = VpnPortOptions(portNumber)

internal fun VpnProtocol.toVpnConnectionProtocol(): VpnConnectionProtocolOptions =
    when (this) {
        OPENVPN -> VpnConnectionProtocolOptions.OPENVPN
        IKEV2 -> VpnConnectionProtocolOptions.IKEV2
        WIREGUARD -> VpnConnectionProtocolOptions.WIREGUARD
    }

internal fun VpnPop.toDomainServerLocation() : ServerLocation {
    return if (city.isNotEmpty()) { CityAndCountryServerLocation(city, country, countryCode) }
    else CountryServerLocation(country, countryCode)
}
