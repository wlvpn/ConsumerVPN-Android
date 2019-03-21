package com.wlvpn.slider.whitelabelvpn.jobs;

import com.evernote.android.job.Job;
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
public class ConsumerVpnJobCreator implements com.evernote.android.job.JobCreator {

    /**
     * 30 seconds to 15 min in milliseconds for debug
     */
    private static final long ONE_MINUTE_IN_MILLISECONDS = 60000L;
    static final long FIFTEEN_MINUTE_IN_MILLISECONDS =
            ONE_MINUTE_IN_MILLISECONDS * 15L;

    /**
     * 30 minutes in Milliseconds
     */
    static final long THIRTY_MINUTES_MILLISECOND = ONE_MINUTE_IN_MILLISECONDS * 30L;

    /**
     * 24 hours in milliseconds
     */
    static final long TWENTY_FOUR_HOURS_IN_MILLISECONDS =
            ONE_MINUTE_IN_MILLISECONDS * 24L * 60L;

    private final CredentialsManager credentialsManager;
    private final ConnectableManager connectableManager;
    private final SettingsManager settingsManager;
    private final AccountManager accountManager;

    @Inject
    public ConsumerVpnJobCreator(CredentialsManager credentialsManager,
                                 ConnectableManager connectableManager,
                                 SettingsManager settingsManager,
                                 AccountManager accountManager) {

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
                        credentialsManager,
                        accountManager,
                        settingsManager,
                        connectableManager);

            case ServersRefreshJob.JOB_ID:
                return new ServersRefreshJob(accountManager);

            default:
                return null;
        }
    }
}

