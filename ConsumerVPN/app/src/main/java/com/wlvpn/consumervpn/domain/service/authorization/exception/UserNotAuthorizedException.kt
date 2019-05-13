package com.wlvpn.consumervpn.domain.service.authorization.exception

class UserNotAuthorizedException : AuthorizationException("User is not authenticated or account has expired")