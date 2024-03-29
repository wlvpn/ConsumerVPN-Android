package com.wlvpn.consumervpn.data.gateway.authorization

import androidx.work.WorkManager
import com.gentlebreeze.vpn.http.api.error.LoginErrorThrowable
import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.wlvpn.consumervpn.data.failure.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.failure.map.ThrowableMapper
import com.wlvpn.consumervpn.data.gateway.authorization.failure.RefreshTokenReLoginFailure
import com.wlvpn.consumervpn.data.util.onErrorMapThrowable
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.data.worker.TokenRefreshWorker
import com.wlvpn.consumervpn.domain.gateway.ExternalAuthorizationGateway
import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable
import io.reactivex.Single

class ExternalAuthorizationGateway(
    private val vpnSdk: IVpnSdk,
    private val workManager: WorkManager
) : ExternalAuthorizationGateway, ThrowableMapper by LocalThrowableMapper() {

    override fun isAccessTokenValid(): Single<Boolean> {
        return Single.just(vpnSdk.isAccessTokenValid())
    }

    override fun refreshToken(credentials: Credentials): Completable = Completable.defer {
        vpnSdk.refreshToken(credentials.username,credentials.password)
            .toSingle()
            .onErrorResumeNext {
                vpnSdk.loginWithUsername(credentials.username, credentials.password)
                    .toSingle()
                    .onErrorMapThrowable { mapThrowable(it) }
            }
            .ignoreElement()
    }

    override fun scheduleRefreshToken() =
        Completable.create { emitter ->
            TokenRefreshWorker.schedule(workManager)
            emitter.onComplete()
        }

    override fun cancelScheduledRefreshToken() =
        Completable.create { emitter ->
            workManager.cancelUniqueWork(TokenRefreshWorker.WORKER_ID)
            emitter.onComplete()
        }

    override fun isAccountExpired(): Single<Boolean> {
        return Single.just(vpnSdk.getAccountInfo().subscriptionEnd)
            .flatMap { subscriptionEnd ->
                val subscriptionEndTime = subscriptionEnd * 1000L
                val currentTime = System.currentTimeMillis()

                Single.just(
                    subscriptionEndTime != 0L
                            && subscriptionEndTime < currentTime
                )
            }
    }

}

/**
 * We want a custom mapper for this gateway, we extend [NetworkThrowableMapper]
 *
 * And map sdk's [LoginErrorThrowable] to [RefreshTokenReLoginFailure] and let [NetworkThrowableMapper] map the rest
 */
private class LocalThrowableMapper : NetworkThrowableMapper() {

    override fun mapThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            is LoginErrorThrowable -> RefreshTokenReLoginFailure()

            else -> super.mapThrowable(throwable)
        }
    }
}