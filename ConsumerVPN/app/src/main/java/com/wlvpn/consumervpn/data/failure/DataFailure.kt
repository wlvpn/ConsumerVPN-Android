package com.wlvpn.consumervpn.data.failure

import com.wlvpn.consumervpn.domain.failure.Failure

/**
 * Basic Data layer Failure, we shouldn't use this failure directly
 *
 * Create a child failure instead
 */
open class DataFailure(message: String?) : Failure(message)