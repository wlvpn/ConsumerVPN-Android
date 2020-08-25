package com.wlvpn.consumervpn.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.netprotect.nativencrkeyption.Encrypter

import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * A credentials repository that encrypts/decrypts credentials with a [Encrypter] which is stored/retrieved from
 * a simple private shared preferences.
 */
class EncryptedSharedPrefsCredentialsRepository(
        baseKeyId: String,
        privateSharedPreferences: SharedPreferences,
        gson: Gson
) : BaseSharedPreferencesRepository(baseKeyId,
        privateSharedPreferences,
        gson
), CredentialsRepository {

    private val credentialsKey get() = baseKeyId + "CREDENTIALS_KEY"

    override fun saveCredentials(credentials: Credentials): Completable = Completable.create {
        //Serialize
        val serializedCredentials = gson.toJson(credentials)
        //Encrypt + encode
        val encryptedCredentials = Encrypter.encryptAndBase64Encode(serializedCredentials)
        //Save
        privateSharedPreferences.edit().putString(credentialsKey, encryptedCredentials).apply()
        it.onComplete()
    }

    override fun getCredentials(): Maybe<Credentials> = Maybe.create {
        // Obtain from preferences
        val encryptedCredentials = privateSharedPreferences.getString(credentialsKey, "")

        if (!encryptedCredentials.isNullOrEmpty()) {
            //Decrypt + decode
            val serializedCredentials = Encrypter.decryptBase64Encoded(encryptedCredentials)
            //Deserialize and emit
            it.onSuccess(gson.fromJson(serializedCredentials, Credentials::class.java))
        }
        it.onComplete()
    }

    override fun hasCredentials(): Single<Boolean> = Single.create {
        it.onSuccess(privateSharedPreferences.contains(credentialsKey))
    }

    override fun deleteCredentials(): Completable = Completable.create {
        privateSharedPreferences.edit().remove(credentialsKey).apply()
        it.onComplete()
    }
}