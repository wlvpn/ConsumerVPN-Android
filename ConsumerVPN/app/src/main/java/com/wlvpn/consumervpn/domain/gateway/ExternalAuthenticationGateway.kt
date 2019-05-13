package com.wlvpn.consumervpn.domain.gateway

import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable

/**
 * A gateway to an external Authentication method.
 */
interface ExternalAuthenticationGateway {

    /**
     * A method to authenticate with provided credentials.
     *
     * @param credentials the credentials to use in authentication
     * @return a [Completable] for this task
     */
    fun authenticate(credentials: Credentials): Completable

    fun logout(): Completable

}