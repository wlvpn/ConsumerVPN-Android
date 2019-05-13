package com.wlvpn.consumervpn.domain.service.authorization

import io.reactivex.Completable
import io.reactivex.Single

interface UserAuthorizationService {

    fun refreshToken(): Completable

    fun scheduleRefreshToken(): Completable

    fun cancelScheduledRefreshToken(): Completable

    fun isAccountExpired(): Single<Boolean>

}