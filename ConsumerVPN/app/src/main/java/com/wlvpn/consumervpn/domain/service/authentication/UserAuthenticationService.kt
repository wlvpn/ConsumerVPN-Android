package com.wlvpn.consumervpn.domain.service.authentication

import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable
import io.reactivex.Single

/**
 * A simple services that manages user authentication.
 */
interface UserAuthenticationService {

    /**
     * Ask if the user is already authenticated.
     *
     * @return a Single for this task emitting a self explanatory boolean.
     */
    fun isAuthenticated(): Single<Boolean>

    /**
     * Authenticates the user with the given [Credentials].
     *
     * @param credentials the credentials to be used in authentication process.
     * @return A Completable for this task.
     */
    fun authenticate(credentials: Credentials): Completable

    fun logout(): Completable

    /**
     *
     * @throws NotAuthenticatedException
     */
    fun getCredentials(): Single<Credentials>

}