package com.wlvpn.consumervpn.data.gateway.connection.failure

import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnConnectionFailure

class NoServersFoundForSelectionFailure : VpnConnectionFailure("Unable to connect, no servers found for selection")