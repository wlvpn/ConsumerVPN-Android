package com.wlvpn.slider.whitelabelvpn.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
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

    private final CredentialsManager credentialsManager;
    private final AccountManager accountManager;
    private final SettingsManager settingsManager;
    private final ConnectableManager connectableManager;

    /**
     * Job ID
     */
    static final String JOB_ID = "TokenRefreshJob";

    /**
     * 30 seconds to 15 min in milliseconds for debug
     */
    private static final long THIRTY_SECONDS_IN_MILLISECONDS = 30000L;
    private static final long FIFTEEN_MINUTE_IN_MILLISECONDS =
            THIRTY_SECONDS_IN_MILLISECONDS * 30L;

    /**
     * 1 hours - 24 hours in milliseconds
     */
    private static final long ONE_HOUR_IN_MILLISECONDS = 3600000L;
    private static final long TWENTY_FOUR_HOURS_IN_MILLISECONDS = ONE_HOUR_IN_MILLISECONDS * 24L;

    private final JobManager jobManager;

    TokenRefreshJob(JobManager jobManager,
                    CredentialsManager credentialsManager,
                    AccountManager accountManager,
                    SettingsManager settingsManager,
                    ConnectableManager connectableManager) {
        this.jobManager = jobManager;
        this.credentialsManager = credentialsManager;
        this.accountManager = accountManager;
        this.settingsManager = settingsManager;
        this.connectableManager = connectableManager;
    }

    /**
     * Schedule the job.
     *
     * @param jobManager schedule manager
     */
    public static void schedule(@NonNull JobManager jobManager) {
        schedule(jobManager, true);
    }

    /**
     * Schedule the job.
     *
     * @param jobManager    schedule manager
     * @param updateCurrent if the current job should be replaced
     */
    private static void schedule(@NonNull JobManager jobManager,
                                 boolean updateCurrent) {
        //For debug builds use reduced timers and more accepting connection type
        long start = BuildConfig.DEBUG ?
                FIFTEEN_MINUTE_IN_MILLISECONDS
                : TWENTY_FOUR_HOURS_IN_MILLISECONDS;

        JobRequest.NetworkType networkType = BuildConfig.DEBUG ?
                JobRequest.NetworkType.CONNECTED
                : JobRequest.NetworkType.UNMETERED;

        jobManager.schedule(new JobRequest.Builder(JOB_ID)
                .setRequiredNetworkType(networkType)
                .setRequirementsEnforced(true)
                .setPeriodic(start)
                .setUpdateCurrent(updateCurrent)
                .build());
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