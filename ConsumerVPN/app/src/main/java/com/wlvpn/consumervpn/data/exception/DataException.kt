package com.wlvpn.consumervpn.data.exception

/**
 * Basic Data layer Exception, we shouldn't use this exception directly
 *
 * Create a child exception instead
 */
open class DataException(message: String?) : RuntimeException(message)