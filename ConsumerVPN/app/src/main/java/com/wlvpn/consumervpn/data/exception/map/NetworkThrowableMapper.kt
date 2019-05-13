package com.wlvpn.consumervpn.data.exception.map

import com.gentlebreeze.http.api.NetworkUnavailableException
import com.wlvpn.consumervpn.data.exception.NetworkNotAvailableException

/**
 * A mapper specialization of [DataThrowableMapper], this will map any sdk network exception to ours
 */
open class NetworkThrowableMapper : DataThrowableMapper() {

    override fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            //Map to our network exception, me shouldn't use sdk one
            is NetworkUnavailableException -> NetworkNotAvailableException()

            //let DataThrowableMapper map the rest
            else -> super.mapThrowable(throwable)
        }
    }

}