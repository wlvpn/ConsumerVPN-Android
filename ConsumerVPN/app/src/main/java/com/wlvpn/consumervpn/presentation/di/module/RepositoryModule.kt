package com.wlvpn.consumervpn.presentation.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.wlvpn.consumervpn.data.repository.EncryptedSharedPrefsCredentialsRepository
import com.wlvpn.consumervpn.data.repository.SharedPrefGeneralConnectionSettingsRepository
import com.wlvpn.consumervpn.data.repository.SharedPrefsConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named

private const val CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE =
        "CREDENTIALS_REPOSITORY_SHARED_PREFERENCES"
/**
 * A module with repository dependencies.
 */
@Module
class RepositoryModule {

    @Provides
    @Named(CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE)
    fun providesCredentislsSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
                application.packageName + CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE,
                Context.MODE_PRIVATE)
    }

    @Provides
    fun providesCredentialsRepository(
            application: Application,
            @Named(CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences
    ):
            CredentialsRepository {
        return EncryptedSharedPrefsCredentialsRepository(
                application.packageName, preferences, Gson()
        )
    }

    @Provides
    fun providesConnectionRequestSettingsRepository(
            application: Application,
            @Named(COMMON_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences
    ):
            ConnectionRequestSettingsRepository {
        return SharedPrefsConnectionRequestSettingsRepository(
                application.packageName, preferences, Gson()
        )
    }

    @Provides
    fun providesGeneralConnectionSettingsRepository(
            application: Application,
            @Named(COMMON_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences
    ):
            GeneralConnectionSettingsRepository {
        return SharedPrefGeneralConnectionSettingsRepository(
                application.packageName, preferences, Gson()
        )
    }

}