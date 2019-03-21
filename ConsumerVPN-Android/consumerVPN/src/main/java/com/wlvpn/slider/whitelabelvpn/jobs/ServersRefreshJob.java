package com.wlvpn.slider.whitelabelvpn.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.wlvpn.slider.whitelabelvpn.BuildConfig;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;

import kotlin.Unit;
import timber.log.Timber;

/**
 * Job implementation for updating the server list.
 */
public class ServersRefreshJob extends Job {

    /**
     * Job ID
     */
    static final String JOB_ID = "ServersRefreshJob";

    private final AccountManager accountManager;

    ServersRefreshJob(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * Schedules the job
     */
    public static void schedule() {
        //For debug builds use reduced timers and more accepting connection type
        long start = BuildConfig.DEBUG ?
                ConsumerVpnJobCreator.FIFTEEN_MINUTE_IN_MILLISECONDS
                : ConsumerVpnJobCreator.THIRTY_MINUTES_MILLISECOND;

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

        if (accountManager.isUserLoggedIn()) {

            // Blocking current thread to allow the refresh token refresh finish
            // before sending Result.Success to Job
            ConsumerVpnApplication.getVpnSdk()
                    .updateServerList()
                    .blockCurrentThread()
                    .subscribe(
                            vpnServersResponse -> Unit.INSTANCE,
                            throwable -> {
                                Timber.e(throwable, "Servers refresh failed during job");
                                return Unit.INSTANCE;
                            });
        }

        return Result.SUCCESS;
    }
}