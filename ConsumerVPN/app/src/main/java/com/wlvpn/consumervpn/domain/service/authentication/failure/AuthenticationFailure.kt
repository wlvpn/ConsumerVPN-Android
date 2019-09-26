package com.wlvpn.consumervpn.domain.service.authentication.failure

import com.wlvpn.consumervpn.domain.failure.DomainFailure

/**
 * An specialization of [DomainFailure], if we want more specific naming, extend this class
 */
open class AuthenticationFailure(message: String) : DomainFailure(message)