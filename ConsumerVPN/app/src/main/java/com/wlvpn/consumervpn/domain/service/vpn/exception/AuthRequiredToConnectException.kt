package com.wlvpn.consumervpn.domain.service.vpn.exception

class AuthRequiredToConnectException : VpnConnectionException("Trying to connect with an authenticated user")