package com.wlvpn.consumervpn.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.wlvpn.consumervpn.data.model.FastestServerLocation
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import io.reactivex.Completable
import io.reactivex.Maybe

class SharedPrefsConnectionRequestSettingsRepository(
        baseKeyId: String,
        privateSharedPreferences: SharedPreferences,
        gson: Gson
) : BaseSharedPreferencesRepository(baseKeyId,
        privateSharedPreferences,
        gson
), ConnectionRequestSettingsRepository {

    private val connectionRequestKey get() = baseKeyId + "CONNECTION_REQUEST_KEY"

    override fun getConnectionRequestSettings(): Maybe<Settings.ConnectionRequest> =
        Maybe.create {
            // Obtain from preferences
            val serializedSettings =
                privateSharedPreferences.getString(connectionRequestKey, "")

            val connectionRequest = if (!serializedSettings.isNullOrEmpty()) {
                //Deserialize
                gson.fromJson(
                    serializedSettings,
                    Settings.ConnectionRequest::class.java
                )
            } else { Settings.ConnectionRequest(location = FastestServerLocation()) }

            it.onSuccess(connectionRequest)
            it.onComplete()
        }

    override fun setConnectionRequestSettings(
        connectionRequest: Settings.ConnectionRequest
    ): Completable = Completable.create {
        //Serialize
        val serializedSettings = gson.toJson(connectionRequest)
        //Save
        privateSharedPreferences.edit().putString(connectionRequestKey, serializedSettings).apply()
        it.onComplete()
    }

    override fun clear(): Completable = Completable.create {
        privateSharedPreferences.edit().remove(connectionRequestKey).apply()
        it.onComplete()
    }
}