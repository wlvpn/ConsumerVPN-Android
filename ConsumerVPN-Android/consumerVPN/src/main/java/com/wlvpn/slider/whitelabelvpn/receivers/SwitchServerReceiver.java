package com.wlvpn.slider.whitelabelvpn.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.helpers.ConnectionHelper;

import javax.inject.Inject;

import kotlin.Unit;
import timber.log.Timber;

public class SwitchServerReceiver extends BroadcastReceiver {

    public static final String ACTION_SWITCH_SERVER = "com.wlvpn.slider.whitelabelvpn.action.SWITCH_SERVERS";

    @Inject
    ConnectionHelper connectionHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_SWITCH_SERVER.equals(intent.getAction())) {
            switchToClosestMatch();
        }
    }

    /**
     * Switch to the closest server matching the users preferences.
     */
    private void switchToClosestMatch() {
        ConsumerVpnApplication.getVpnSdk().fetchConnectionInfo()
                .subscribe(connectionInfo -> {
                    connectionHelper.connectByCountryCode(connectionInfo.getCountryCode());
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to fetch connection info");
                    return Unit.INSTANCE;
                });
    }
}
