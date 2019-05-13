package com.wlvpn.consumervpn.domain.service.servers.exception

import com.wlvpn.consumervpn.domain.exception.DomainException

/**
 * An specialization of [DomainException], if we want more specific naming, extend this class
 */
open class ServerException(message: String) : DomainException(message)