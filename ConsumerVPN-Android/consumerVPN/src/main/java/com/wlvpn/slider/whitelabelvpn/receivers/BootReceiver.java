package com.wlvpn.slider.whitelabelvpn.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wlvpn.slider.whitelabelvpn.startup.Startup;


public class BootReceiver extends BroadcastReceiver {

    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_ACTION.equals(intent.getAction())) {
            Startup.boot(context);
        }
    }
}
