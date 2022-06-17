package com.wlvpn.consumervpn.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.netprotect.nativencrkeyption.Encrypter
import com.wlvpn.consumervpn.data.failure.InsufficientRepositoryResourcesFailure

import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.lang.ref.WeakReference

/**
 * A credentials repository that encrypts/decrypts credentials with a [Encrypter] which is stored/retrieved from
 * a simple private shared preferences.
 */
class EncryptedSharedPrefsCredentialsRepository(
        baseKeyId: String,
        privateSharedPreferences: SharedPreferences,
        gson: Gson,
        private val context: WeakReference<Context>
) : BaseSharedPreferencesRepository(baseKeyId,
        privateSharedPreferences,
        gson
), CredentialsRepository {

    private val credentialsKey get() = baseKeyId + "CREDENTIALS_KEY"

    override fun saveCredentials(credentials: Credentials): Completable = Completable.create {

        context.get()?.let { context ->
            //Serialize
            val serializedCredentials = gson.toJson(credentials)
            //Encrypt + encode
            val encryptedCredentials = Encrypter.encryptAndBase64Encode(
                context,
                serializedCredentials
            )
            //Save
            privateSharedPreferences.edit().putString(credentialsKey, encryptedCredentials).apply()
            it.onComplete()
        } ?: run {
            it.onError(InsufficientRepositoryResourcesFailure())
        }
    }

    override fun getCredentials(): Maybe<Credentials> = Maybe.create {

        context.get()?.let { context ->
            // Obtain from preferences
            val encryptedCredentials = privateSharedPreferences.getString(credentialsKey, "")

            if (!encryptedCredentials.isNullOrEmpty()) {
                //Decrypt + decode
                val serializedCredentials = Encrypter.decryptBase64Encoded(
                    context,
                    encryptedCredentials
                )
                //Deserialize and emit
                it.onSuccess(gson.fromJson(serializedCredentials, Credentials::class.java))
            }
            it.onComplete()
        } ?: run {
            it.onError(InsufficientRepositoryResourcesFailure())
        }

    }

    override fun hasCredentials(): Single<Boolean> = Single.create {
        it.onSuccess(privateSharedPreferences.contains(credentialsKey))
    }

    override fun deleteCredentials(): Completable = Completable.create {
        privateSharedPreferences.edit().remove(credentialsKey).apply()
        it.onComplete()
    }
}