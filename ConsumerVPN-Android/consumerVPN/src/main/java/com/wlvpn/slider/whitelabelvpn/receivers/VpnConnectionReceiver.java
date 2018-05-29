package com.wlvpn.slider.whitelabelvpn.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;

import kotlin.Unit;
import timber.log.Timber;


public class VpnConnectionReceiver extends BroadcastReceiver {

    public static final String ACTION_DISCONNECT = "com.wlvpn.slider.whitelabelvpn.action.DISCONNECT";


    public VpnConnectionReceiver() {
        ConsumerVpnApplication.component().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_DISCONNECT:
                    disconnect();
                    break;
            }
        }
    }

    /**
     * Disconnect from the VPN
     */
    private void disconnect() {
        if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
            ConsumerVpnApplication.getVpnSdk().disconnect()
                    .subscribe(aBoolean -> {
                        Timber.d("Disconnected from Receiver");
                        return Unit.INSTANCE;
                    }, throwable -> {
                        Timber.e(throwable, "Failed to disconnect");
                        return Unit.INSTANCE;
                    });
        }
    }
}
