package com.wlvpn.consumervpn.data.gateway.connection.exception

import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnConnectionException

class NoServersFoundForSelectionException : VpnConnectionException("Unable to connect, no servers found for selection")