package com.wlvpn.consumervpn.domain.service.authorization

import com.wlvpn.consumervpn.domain.gateway.ExternalAuthorizationGateway
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.service.authorization.exception.UserNotAuthorizedException
import io.reactivex.Completable
import io.reactivex.Single

class ExternalUserAuthorizationService(
    private val credentialsRepository: CredentialsRepository,
    private val externalAuthorizationGateway: ExternalAuthorizationGateway
) : UserAuthorizationService {

    override fun refreshToken(): Completable {
        return credentialsRepository.hasCredentials() // If is user logged in
            .map { isAuthenticated ->
                if (!isAuthenticated) {
                    throw UserNotAuthorizedException()
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
                    throw UserNotAuthorizedException()
                }

                externalAuthorizationGateway.isAccountExpired()
            }
    }

}