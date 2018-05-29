package com.wlvpn.slider.whitelabelvpn.managers;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.Unit;
import timber.log.Timber;

@Singleton
public class AccountManager {

    private final CredentialsManager credentialsManager;

    @Inject
    public AccountManager(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    public boolean isUserLoggedIn() {
        return credentialsManager.hasCredentials();
    }

    public void logout() {
        credentialsManager.removeCredentials();
        ConsumerVpnApplication.getVpnSdk().logout()
                .subscribe(unit -> {
                    //do nothing
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to logout");
                    return Unit.INSTANCE;
                });
    }
}
