package com.wlvpn.consumervpn.domain.repository

import com.wlvpn.consumervpn.domain.model.Credentials
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * A repository of [Credentials]
 */
interface CredentialsRepository {

    /**
     * Persist credentials.
     *
     * @param credentials the credentials.
     * @return a Completable of this task.
     */
    fun saveCredentials(credentials: Credentials): Completable

    /**
     * Gets persisted credentials.
     *
     * @return a Maybe of this task, could return or not credentials.
     */
    fun getCredentials(): Maybe<Credentials>

    /**
     * Ask to the repo if it has stored credential.
     *
     * @return a Single for this task emitting a self explanatory boolean.
     */
    fun hasCredentials(): Single<Boolean>

    /**
     * Deletes credentials persisted by this repository.
     *
     * @return A completable for this task.
     */
    fun deleteCredentials(): Completable

}