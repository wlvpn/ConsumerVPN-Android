package com.wlvpn.slider.whitelabelvpn.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.gentlebreeze.vpn.http.api.error.LoginErrorThrowable;
import com.wlvpn.slider.whitelabelvpn.BuildConfig;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.auth.Credentials;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.models.ApiErrorCodes;

import kotlin.Unit;
import timber.log.Timber;

/**
 * Job implementation for updating the server list.
 */
public class TokenRefreshJob extends Job {

    /**
     * Job ID
     */
    static final String JOB_ID = "TokenRefreshJob";

    private final CredentialsManager credentialsManager;
    private final AccountManager accountManager;
    private final SettingsManager settingsManager;
    private final ConnectableManager connectableManager;

    TokenRefreshJob(CredentialsManager credentialsManager,
                    AccountManager accountManager,
                    SettingsManager settingsManager,
                    ConnectableManager connectableManager) {
        this.credentialsManager = credentialsManager;
        this.accountManager = accountManager;
        this.settingsManager = settingsManager;
        this.connectableManager = connectableManager;
    }

    /**
     * Schedules the job.
     */
    public static void schedule() {
        //For debug builds use reduced timers and more accepting connection type
        long start = BuildConfig.DEBUG ?
                ConsumerVpnJobCreator.FIFTEEN_MINUTE_IN_MILLISECONDS
                : ConsumerVpnJobCreator.TWENTY_FOUR_HOURS_IN_MILLISECONDS;

        JobRequest.NetworkType networkType = BuildConfig.DEBUG ?
                JobRequest.NetworkType.CONNECTED
                : JobRequest.NetworkType.UNMETERED;

        new JobRequest.Builder(JOB_ID)
                .setRequiredNetworkType(networkType)
                .setRequirementsEnforced(true)
                .setPeriodic(start)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        if (accountManager.isUserLoggedIn()
                && !ConsumerVpnApplication.getVpnSdk().isAccessTokenValid()) {

            // Blocking current thread to allow the refresh token refresh finish
            // before sending Result.Success to Job
            ConsumerVpnApplication.getVpnSdk()
                    .refreshToken()
                    .blockCurrentThread()
                    .subscribe(
                            vpnLoginResponse -> Unit.INSTANCE,
                            throwable -> {
                                Timber.e(throwable, "Token refresh failed during job");
                                tokenFailLogin();
                                return Unit.INSTANCE;
                            });
        }

        return Result.SUCCESS;
    }

    /**
     * Will update db values and will logout user whenever credentials changed on server
     */
    private void tokenFailLogin() {
        Credentials credentials = credentialsManager.getCredentials();

        // Blocking current thread to allow the login finish
        // before sending Result.Success to Job
        ConsumerVpnApplication.getVpnSdk()
                .loginWithUsername(
                        credentials.getUsername(),
                        credentials.getPassword())
                .blockCurrentThread()
                .subscribe(
                        vpnLoginResponse -> Unit.INSTANCE,
                        throwable -> {
                            if (throwable instanceof LoginErrorThrowable) {
                                LoginErrorThrowable loginThrowable =
                                        (LoginErrorThrowable) throwable;
                                // When login fails that means user updated
                                // it's credentials on web the user will be logged out
                                if (loginThrowable.getResponseCode() ==
                                        ApiErrorCodes.INVALID_CREDENTIALS) {

                                    if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                                        ConsumerVpnApplication.getVpnSdk()
                                                .disconnect();
                                    }
                                    accountManager.logout();
                                    settingsManager.clear();
                                    connectableManager.clear();

                                    ConsumerVpnApplication.getVpnSdk().logout();
                                }
                            }
                            return Unit.INSTANCE;
                        });
    }
}