package com.wlvpn.consumervpn.data.gateway.authorization.exception

import com.wlvpn.consumervpn.domain.service.authorization.exception.AuthorizationException

class RefreshTokenReLoginException : AuthorizationException("Unable to Re-login with server")