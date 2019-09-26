package com.wlvpn.consumervpn.data.gateway.connection.failure

import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnConnectionFailure

class ServerNotSelectedToVpnFailure : VpnConnectionFailure("Unable to connect, server not selected")