package com.wlvpn.slider.whitelabelvpn.di;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.gentlebreeze.db.sqlite.DatabaseImpl;
import com.gentlebreeze.db.sqlite.GetDatabase;
import com.gentlebreeze.db.sqlite.IDatabase;
import com.gentlebreeze.db.sqlite.IMigrationEvent;
import com.gentlebreeze.db.sqlite.MigrationManager;
import com.gentlebreeze.http.api.ApiAuthRequest;
import com.gentlebreeze.http.api.AuthRequestExecutorFunction;
import com.gentlebreeze.http.api.ResponseFunction;
import com.gentlebreeze.http.connectivity.ConnectivityNetworkStateProvider;
import com.gentlebreeze.http.connectivity.INetworkStateProvider;
import com.gentlebreeze.vpn.db.sqlite.tables.FavoriteTable;
import com.gentlebreeze.vpn.db.sqlite.tables.PingTable;
import com.gentlebreeze.vpn.db.sqlite.tables.PopTable;
import com.gentlebreeze.vpn.db.sqlite.tables.ProtocolTable;
import com.gentlebreeze.vpn.db.sqlite.tables.ServerProtocolTable;
import com.gentlebreeze.vpn.db.sqlite.tables.ServerStatusTable;
import com.gentlebreeze.vpn.db.sqlite.tables.ServerTable;
import com.gentlebreeze.vpn.http.interactor.update.UpdateDatabase;
import com.wlvpn.slider.whitelabelvpn.BuildConfig;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.adapters.EncryptionPagerAdapter;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.helpers.ConnectionHelper;
import com.wlvpn.slider.whitelabelvpn.holders.EncryptionPagerHolder;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.NavigationManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.managers.VpnNotificationManager;
import com.wlvpn.slider.whitelabelvpn.settings.CipherPref;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class AppModule {

    private Application application;

    public void setApplication(final Application application) {
        this.application = application;
    }

    @Provides
    public Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    public OkHttpClient getOkHttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    public CredentialsManager provideAuthManager(Context context) {
        return new CredentialsManager(context);
    }

    @Provides
    INetworkStateProvider provideNetworkStateProvider(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return new ConnectivityNetworkStateProvider(cm);
    }

    @Provides
    @Singleton
    NavigationManager provideNavigationProvider() {
        return new NavigationManager();
    }

    @Provides
    UpdateDatabase provideUpdateDatabase(UpdateDatabase updateDatabase) {
        return updateDatabase;
    }

    @Provides
    @Singleton
    IDatabase provideDatabase() {
        DatabaseImpl database = new DatabaseImpl(
                ConsumerVpnApplication.DB_NAME,
                ConsumerVpnApplication.DB_VERSION);
        database.addTable(new ServerTable());
        database.addTable(new PopTable());
        database.addTable(new ProtocolTable());
        database.addTable(new ServerProtocolTable());
        database.addTable(new FavoriteTable());
        database.addTable(new ServerStatusTable());
        database.addTable(new PingTable());
        return database;
    }

    @Provides
    @Singleton
    GetDatabase provideGetDatabase(Context context, IDatabase database, MigrationManager migrationManager) {
        return new GetDatabase(context, database, migrationManager);
    }

    @Provides
    MigrationManager provideMigrationManager() {
        LinkedList<IMigrationEvent> migrationList = new LinkedList<>();
        return new MigrationManager(migrationList);
    }

    @Provides
    ResponseFunction provideResponseFunction() {
        return new ResponseFunction();
    }

    @Provides
    ApiAuthRequest provideAuthRequest(AuthRequestExecutorFunction authRequestExecutorFunction,
                                      ResponseFunction responseFunction) {
        return new ApiAuthRequest(authRequestExecutorFunction, responseFunction);
    }

    @Provides
    SettingsManager provideSettingsManager(Context context) {
        return new SettingsManager(context);
    }

    @Provides
    NotificationManager providesNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    ConnectionHelper providesConnectionHelper(VpnNotificationManager notificationManager,
                                              CredentialsManager credentialsManager,
                                              SettingsManager settingsManager) {

        return new ConnectionHelper(notificationManager, credentialsManager, settingsManager);
    }

    @Provides
    AccountManager providesAccountManager(CredentialsManager credentialsManager) {
        return new AccountManager(credentialsManager);
    }

    @Singleton
    @Provides
    List<EncryptionPagerHolder> providesEncrryptionTabs(SettingsManager settingsManager) {
        List<EncryptionPagerHolder> tabsList = new ArrayList<>();

        if (BuildConfig.CONNECTION_TABS.get(0)) {
            tabsList.add(new EncryptionPagerHolder(
                    R.raw.ic_secure, R.string.encryption_secure_title, R.string.secure));
        } else if (settingsManager.getCipherPref().getCipher() == CipherPref.CIPHER_AES256) {
            settingsManager.updateCipher(new CipherPref(CipherPref.CIPHER_AES128));
        }

        if (BuildConfig.CONNECTION_TABS.get(1)) {
            tabsList.add(new EncryptionPagerHolder(
                    R.raw.ic_fast, R.string.encryption_fast_title, R.string.fast));
        } else if (settingsManager.getCipherPref().getCipher() == CipherPref.CIPHER_AES128) {
            settingsManager.updateCipher(new CipherPref(CipherPref.CIPHER_NONE));
        }

        if (BuildConfig.CONNECTION_TABS.get(2)) {
            tabsList.add(new EncryptionPagerHolder(
                    R.raw.ic_fastest, R.string.encryption_none_title, R.string.fastest));

        } else if (settingsManager.getCipherPref().getCipher() == CipherPref.CIPHER_NONE) {
            if (BuildConfig.CONNECTION_TABS.get(1)) {
                settingsManager.updateCipher(new CipherPref(CipherPref.CIPHER_AES128));
            } else {
                settingsManager.updateCipher(new CipherPref(CipherPref.CIPHER_AES256));
            }
        }

        return tabsList;
    }

    @Provides
    EncryptionPagerAdapter providesEncryptionAdapter(List<EncryptionPagerHolder> tabsList) {
        return new EncryptionPagerAdapter(tabsList);
    }
}