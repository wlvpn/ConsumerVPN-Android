package com.wlvpn.consumervpn.domain.service.authorization

import com.wlvpn.consumervpn.domain.gateway.ExternalAuthorizationGateway
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.service.authorization.failure.UserNotAuthorizedFailure
import io.reactivex.Completable
import io.reactivex.Single

class ExternalUserAuthorizationService(
    private val credentialsRepository: CredentialsRepository,
    private val externalAuthorizationGateway: ExternalAuthorizationGateway
) : UserAuthorizationService {

    override fun refreshToken(): Completable {
        return credentialsRepository.hasCredentials() // If is user logged in
            .flatMap { isAuthenticated ->
                if (!isAuthenticated) {
                    Single.error(UserNotAuthorizedFailure())
                } else {
                    Single.just(isAuthenticated)
                }
            }
            .flatMap {
                externalAuthorizationGateway.isAccessTokenValid()
            }
            .filter { isValid -> (!isValid) }
            .flatMap {
                credentialsRepository.getCredentials()
            }.flatMapCompletable { credentials ->
                externalAuthorizationGateway.refreshToken(credentials)
            }
    }

    override fun scheduleRefreshToken() = externalAuthorizationGateway.scheduleRefreshToken()

    override fun cancelScheduledRefreshToken() = externalAuthorizationGateway.cancelScheduledRefreshToken()

    override fun isAccountExpired(): Single<Boolean> {
        return credentialsRepository.hasCredentials() // If is user logged in
            .flatMap { isAuthenticated ->
                if (!isAuthenticated) {
                    Single.error(UserNotAuthorizedFailure())
                } else {
                    externalAuthorizationGateway.isAccountExpired()
                }
            }
    }

}