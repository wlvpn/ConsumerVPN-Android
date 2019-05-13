package com.wlvpn.consumervpn.domain.exception

/**
 * Basic Domain layer Exception, we shouldn't use this exception directly
 *
 * Create a child exception instead.
 */
open class DomainException(message: String?) : RuntimeException(message)