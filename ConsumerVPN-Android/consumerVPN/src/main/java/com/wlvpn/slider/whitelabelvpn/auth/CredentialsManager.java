package com.wlvpn.slider.whitelabelvpn.auth;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.utilities.AESUtil;

import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class CredentialsManager {

    private static final String SHARED_PREFERENCES_NAMESPACE = "User-Area";
    private static final String AUTH_USERNAME = "USERNAME";
    private static final String AUTH_PASSWORD = "PASSWORD";

    private final SharedPreferences sharedPreferences;

    @Inject
    public CredentialsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAMESPACE, Context.MODE_PRIVATE);
    }

    public Credentials getCredentials() {
        String username = sharedPreferences.getString(AUTH_USERNAME, null);
        String password = sharedPreferences.getString(AUTH_PASSWORD, null);

        String keyPass = ConsumerVpnApplication.getVpnSdk()
                .getDeviceInfo()
                .getUuid();

        try {
            if (username != null && password != null) {
                username = AESUtil.decrypt(keyPass, username);
                password = AESUtil.decrypt(keyPass, password);
            }
        } catch (GeneralSecurityException ex) {
            Timber.e(ex);
        }

        return new Credentials(username, password);
    }

    public void setCredentials(@NonNull Credentials credentials) {
        try {
            String keyPass = ConsumerVpnApplication.getVpnSdk()
                    .getDeviceInfo()
                    .getUuid();

            String encryptedUser = credentials.getUsername() != null ?
                    AESUtil.encrypt(keyPass, credentials.getUsername()) : null;

            String encryptedPassword = credentials.getPassword() != null ?
                    AESUtil.encrypt(keyPass, credentials.getPassword()) : null;

            sharedPreferences.edit()
                    .putString(AUTH_USERNAME, encryptedUser)
                    .putString(AUTH_PASSWORD, encryptedPassword)
                    .apply();

        } catch (GeneralSecurityException ex) {
            Timber.e(ex);

            sharedPreferences.edit()
                    .putString(AUTH_USERNAME, credentials.getUsername())
                    .putString(AUTH_PASSWORD, credentials.getPassword())
                    .apply();
        }
    }

    public void removeCredentials() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }

    public boolean hasCredentials() {
        Credentials credentials = getCredentials();
        return (credentials.getUsername() != null
                && credentials.getPassword() != null);
    }
}
