package com.wlvpn.consumervpn.data.gateway.connection.exception

import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnConnectionException

class ServerNotSelectedToVpnException : VpnConnectionException("Unable to connect, server not selected")