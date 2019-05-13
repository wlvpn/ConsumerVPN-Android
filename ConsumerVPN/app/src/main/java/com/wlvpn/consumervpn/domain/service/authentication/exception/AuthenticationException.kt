package com.wlvpn.consumervpn.domain.service.authentication.exception

import com.wlvpn.consumervpn.domain.exception.DomainException

/**
 * An specialization of [DomainException], if we want more specific naming, extend this class
 */
open class AuthenticationException(message: String) : DomainException(message)