package com.wlvpn.slider.whitelabelvpn.di;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.gentlebreeze.http.api.ApiAuthRequest;
import com.gentlebreeze.http.api.AuthRequestExecutorFunction;
import com.gentlebreeze.http.api.ResponseFunction;
import com.gentlebreeze.http.connectivity.ConnectivityNetworkStateProvider;
import com.gentlebreeze.http.connectivity.INetworkStateProvider;
import com.wlvpn.slider.whitelabelvpn.BuildConfig;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.adapters.EncryptionPagerAdapter;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.helpers.ConnectionHelper;
import com.wlvpn.slider.whitelabelvpn.holders.EncryptionPagerHolder;
import com.wlvpn.slider.whitelabelvpn.jobs.ConsumerVpnJobCreator;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.NavigationManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.managers.VpnNotificationManager;
import com.wlvpn.slider.whitelabelvpn.settings.CipherPref;
import com.wlvpn.slider.whitelabelvpn.utilities.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public static final String IS_DEVICE_TV_PROPERTY = "IS_DEVICE_TV_PROPERTY";

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    CredentialsManager provideAuthManager(Context context) {
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
    List<EncryptionPagerHolder> providesEncryptionTabs(SettingsManager settingsManager) {
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

    @Provides
    ConsumerVpnJobCreator providesJobCreator(CredentialsManager credentialsManager,
                                             ConnectableManager connectableManager,
                                             SettingsManager settingsManager,
                                             AccountManager accountManager) {

        return new ConsumerVpnJobCreator(
                credentialsManager,
                connectableManager,
                settingsManager,
                accountManager);
    }

    @Provides
    @Singleton
    @Named(IS_DEVICE_TV_PROPERTY)
    boolean providesIsDeviceTv(Context context) {
        return DeviceUtils.isTv(context);
    }
}