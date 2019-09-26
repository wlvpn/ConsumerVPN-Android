package com.wlvpn.consumervpn.domain.failure

/**
 * Basic Domain layer Failure, we shouldn't use this failure directly
 *
 * Create a child failure instead.
 */
open class DomainFailure(message: String?) : Failure(message)