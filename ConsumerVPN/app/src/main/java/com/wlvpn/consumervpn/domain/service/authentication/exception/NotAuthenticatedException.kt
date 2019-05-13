package com.wlvpn.consumervpn.domain.service.authentication.exception

class NotAuthenticatedException : AuthenticationException("Requested feature requires user to be authenticated first")