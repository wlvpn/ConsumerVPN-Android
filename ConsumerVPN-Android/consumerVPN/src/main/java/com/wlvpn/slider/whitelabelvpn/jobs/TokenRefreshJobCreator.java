package com.wlvpn.slider.whitelabelvpn.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Job creator for server list based jobs.
 */
@Singleton
public class TokenRefreshJobCreator implements JobCreator {

    private final JobManager jobManager;
    private final CredentialsManager credentialsManager;
    private final ConnectableManager connectableManager;
    private final SettingsManager settingsManager;
    private final AccountManager accountManager;

    @Inject
    public TokenRefreshJobCreator(JobManager jobManager,
                                  CredentialsManager credentialsManager,
                                  ConnectableManager connectableManager,
                                  SettingsManager settingsManager,
                                  AccountManager accountManager) {
        this.jobManager = jobManager;
        this.credentialsManager = credentialsManager;
        this.settingsManager = settingsManager;
        this.connectableManager = connectableManager;
        this.accountManager = accountManager;
    }

    @Override
    public Job create(String tag) {
        switch (tag) {
            case TokenRefreshJob.JOB_ID:
                return new TokenRefreshJob(
                        jobManager,
                        credentialsManager,
                        accountManager,
                        settingsManager,
                        connectableManager);
            default:
                return null;
        }
    }
}

