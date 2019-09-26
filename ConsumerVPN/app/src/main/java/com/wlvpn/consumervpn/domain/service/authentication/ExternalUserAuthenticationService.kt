package com.wlvpn.consumervpn.domain.service.authentication

import com.wlvpn.consumervpn.domain.gateway.ExternalAuthenticationGateway
import com.wlvpn.consumervpn.domain.gateway.ExternalAuthorizationGateway
import com.wlvpn.consumervpn.domain.gateway.ExternalServersGateway
import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import com.wlvpn.consumervpn.domain.service.authentication.failure.NotAuthenticatedFailure
import io.reactivex.Completable
import io.reactivex.Single

/**
 * External authorization implementation.
 */
class ExternalUserAuthenticationService(
    private val credentialsRepository: CredentialsRepository,
    private val connectionRequestSettingsRepository: ConnectionRequestSettingsRepository,
    private val generalConnectionSettingsRepository: GeneralConnectionSettingsRepository,
    private val externalAuthenticationGateway: ExternalAuthenticationGateway,
    private val authorizationGateway: ExternalAuthorizationGateway,
    private val serversGateway: ExternalServersGateway
) : UserAuthenticationService {

    override fun isAuthenticated(): Single<Boolean> = credentialsRepository.hasCredentials()

    override fun authenticate(credentials: Credentials): Completable =
    // Ask gateway to login
        externalAuthenticationGateway.authenticate(credentials)
            //Then save credentials
            .andThen(credentialsRepository.saveCredentials(credentials))

    override fun logout(): Completable {
        return credentialsRepository.deleteCredentials()
            // Clear any saved setting
            .andThen(connectionRequestSettingsRepository.clear())
            .andThen(generalConnectionSettingsRepository.clear())
            // Cancel any remaining scheduled operations
            .andThen(serversGateway.cancelScheduledRefreshServers())
            .andThen(authorizationGateway.cancelScheduledRefreshToken())
            .andThen(externalAuthenticationGateway.logout())
    }

    override fun getCredentials(): Single<Credentials> =
        credentialsRepository
            .getCredentials()
            .switchIfEmpty(Single.error(NotAuthenticatedFailure()))

}