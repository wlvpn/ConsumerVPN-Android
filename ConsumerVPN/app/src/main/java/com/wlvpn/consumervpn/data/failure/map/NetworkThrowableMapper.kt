package com.wlvpn.consumervpn.data.failure.map

import com.gentlebreeze.http.api.NetworkUnavailableException
import com.wlvpn.consumervpn.data.failure.NetworkNotAvailableFailure

/**
 * A mapper specialization of [DataThrowableMapper], this will map any sdk network exception to ours
 */
open class NetworkThrowableMapper : DataThrowableMapper() {

    override fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            //Map to our network exception, me shouldn't use sdk one
            is NetworkUnavailableException -> NetworkNotAvailableFailure()

            //let DataThrowableMapper map the rest
            else -> super.mapThrowable(throwable)
        }
    }

}