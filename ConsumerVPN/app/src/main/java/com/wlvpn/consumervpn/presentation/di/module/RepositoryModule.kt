package com.wlvpn.consumervpn.presentation.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wlvpn.consumervpn.data.model.ServerLocationAdapter
import com.wlvpn.consumervpn.data.repository.EncryptedSharedPrefsCredentialsRepository
import com.wlvpn.consumervpn.data.repository.SharedPrefGeneralConnectionSettingsRepository
import com.wlvpn.consumervpn.data.repository.SharedPrefsConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import dagger.Module
import dagger.Provides
import java.lang.ref.WeakReference
import javax.inject.Named


private const val CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE =
        "CREDENTIALS_REPOSITORY_SHARED_PREFERENCES"
private const val GSON_CONNECTION_REQUEST_SERIALIZER =
        "GSON_CONNECTION_REQUEST_SERIALIZER"
private const val DEFAULT_GSON =
        "DEFAULT_GSON"
/**
 * A module with repository dependencies.
 */
@Module
class RepositoryModule {

    @Provides
    @Named(CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE)
    fun providesCredentialsSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
                application.packageName + CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE,
                Context.MODE_PRIVATE)
    }

    @Provides
    fun providesCredentialsRepository(
            application: Application,
            @Named(CREDENTIALS_REPOSITORY_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences,
            @Named(DEFAULT_GSON)
            gson: Gson
    ):
            CredentialsRepository {
        return EncryptedSharedPrefsCredentialsRepository(
                application.packageName, preferences, gson, WeakReference(application)
        )
    }

    @Provides
    fun providesConnectionRequestSettingsRepository(
            application: Application,
            @Named(COMMON_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences,
            @Named(GSON_CONNECTION_REQUEST_SERIALIZER)
            gson: Gson
    ):
            ConnectionRequestSettingsRepository {
        return SharedPrefsConnectionRequestSettingsRepository(
                application.packageName, preferences, gson
        )
    }

    @Provides
    fun providesGeneralConnectionSettingsRepository(
            application: Application,
            @Named(COMMON_SHARED_PREFERENCES_NAMESPACE)
            preferences: SharedPreferences,
            @Named(DEFAULT_GSON)
            gson: Gson
    ):
            GeneralConnectionSettingsRepository {
        return SharedPrefGeneralConnectionSettingsRepository(
                application.packageName, preferences, gson
        )
    }

    /**
     * A specific serializer is provided for ServerLocation subclasses since they are unknown to
     * the domain layer, being polymorphic subclasses derived from the ServerLocation interface
     * the result is that Gson lib is not able to deduce which subclass should be serialized,
     * using a Gson serializer adapter allows to the domain layer to be versatile and provide
     * extensible capabilities while keeping object serialization functionality.
     */
    @Provides
    @Named(GSON_CONNECTION_REQUEST_SERIALIZER)
    fun providesConnectionRequestGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                ServerLocation::class.java,
                ServerLocationAdapter()
            )
            .create()
    }

    @Provides
    @Named(DEFAULT_GSON)
    fun providesDefaultGson(): Gson {
        return Gson()
    }
}