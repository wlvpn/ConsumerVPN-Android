package com.wlvpn.consumervpn.domain.service.vpn.exception

import com.wlvpn.consumervpn.domain.failure.DomainFailure

/**
 * An specialization of [DomainFailure], if we want more specific naming, extend this class
 */
open class VpnConnectionFailure(message: String) : DomainFailure(message)