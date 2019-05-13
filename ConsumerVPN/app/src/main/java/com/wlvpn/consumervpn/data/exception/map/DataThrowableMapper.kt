package com.wlvpn.consumervpn.data.exception.map

import com.wlvpn.consumervpn.data.exception.DataException
import com.wlvpn.consumervpn.data.exception.UnknownErrorException
import com.wlvpn.consumervpn.domain.exception.DomainException


/**
 * This mapper passes through data and domain exceptions anything else is thrown as UnknownErrorException
 *
 * If you need a more specific map, feel free to extend this class
 */
open class DataThrowableMapper : ThrowableMapper {

    override fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {

            // No need to map our exceptions
            is DomainException -> throwable

            // No need to map our exceptions
            is DataException -> throwable

            // Currently, sdk throws a BaseErrorThrowable with a message that is shown to the user...
            // Exception messages are for developers, not for final users
            // For now, we map this as an UnknownErrorException with a optional message
            else -> UnknownErrorException(throwable.message)
        }
    }

}