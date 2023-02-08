package com.wlvpn.consumervpn.domain.model

private const val OPENVPN_VPN_PROTOCOL_NAME = "OPENVPN"
private const val IKEV2_VPN_PROTOCOL_NAME = "IKEV2"
private const val WIREGUARD_VPN_PROTOCOL_NAME = "WIREGUARD"

enum class VpnProtocol(
    val protocolName: String
) {

    OPENVPN(OPENVPN_VPN_PROTOCOL_NAME),
    IKEV2(IKEV2_VPN_PROTOCOL_NAME),
    WIREGUARD(WIREGUARD_VPN_PROTOCOL_NAME),
}