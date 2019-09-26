package com.wlvpn.consumervpn.data.failure.map

import com.wlvpn.consumervpn.data.failure.DataFailure
import com.wlvpn.consumervpn.data.failure.UnknownErrorException
import com.wlvpn.consumervpn.domain.failure.DomainFailure

/**
 * This mapper passes through data and domain [Failure]s anything else is thrown as
 *
 * UnknownErrorException, if you need a more specific map, feel free to extend this class
 */
open class DataThrowableMapper : ThrowableMapper {

    override fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {

            // No need to map our failures
            is DomainFailure -> throwable

            // No need to map our failures
            is DataFailure -> throwable

            // Currently, sdk throws a BaseErrorThrowable with a message that is shown to the user...
            // Exception messages are for developers, not for final users
            // For now, we map this as an UnknownErrorException with a optional message
            else -> UnknownErrorException(throwable.message)
        }
    }
}