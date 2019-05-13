package com.wlvpn.consumervpn.domain.gateway

import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable
import io.reactivex.Single

/**
 * A gateway to an external Authentication method.
 */
interface ExternalAuthorizationGateway {

    fun refreshToken(credentials: Credentials): Completable

    fun isAccessTokenValid(): Single<Boolean>

    fun scheduleRefreshToken(): Completable

    fun cancelScheduledRefreshToken(): Completable

    fun isAccountExpired(): Single<Boolean>

}