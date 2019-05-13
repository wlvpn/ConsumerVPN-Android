package com.wlvpn.consumervpn.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import io.reactivex.Completable
import io.reactivex.Maybe

class SharedPrefGeneralConnectionSettingsRepository(
        baseKeyId: String,
        privateSharedPreferences: SharedPreferences,
        gson: Gson
) : BaseSharedPreferencesRepository(baseKeyId,
        privateSharedPreferences,
        gson
), GeneralConnectionSettingsRepository {

    private val settingsKey get() = baseKeyId + "SETTINGS_KEY"

    override fun getGeneralSettings(): Maybe<Settings.GeneralConnection> =
        Maybe.create {
            // Obtain from preferences
            val serializedSettings = privateSharedPreferences.getString(settingsKey, "")

            if (!serializedSettings.isNullOrEmpty()) {
                //Deserialize and emit
                it.onSuccess(gson.fromJson(serializedSettings, Settings.GeneralConnection::class.java))
            }
            it.onComplete()
        }

    override fun setGeneralConnectionSettings(
        generalConnection: Settings.GeneralConnection
    ): Completable = Completable.create {
        //Serialize
        val serializedSettings = gson.toJson(generalConnection)
        //Save
        privateSharedPreferences.edit().putString(settingsKey, serializedSettings).apply()
        it.onComplete()
    }

    override fun clear(): Completable = Completable.create {
        privateSharedPreferences.edit().remove(settingsKey).apply()
        it.onComplete()
    }
}