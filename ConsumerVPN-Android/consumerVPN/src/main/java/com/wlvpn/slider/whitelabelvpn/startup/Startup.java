package com.wlvpn.slider.whitelabelvpn.startup;

import android.content.Context;
import android.content.Intent;

import com.wlvpn.slider.whitelabelvpn.activities.MainActivity;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.settings.ConnectionStartupPref;

public class Startup {

    public static final String FLAG_INITIAL_BOOT = "initial_boot";

    /**
     * Boot check
     *
     * @param context receiver context
     */
    public static void boot(Context context) {
        if (shouldStartupOnBoot(context)) {
            bootToMainActivity(context);
        }
    }

    /**
     * Boots to main activity with initial boot flag
     *
     * @param context receiver context
     */
    private static void bootToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FLAG_INITIAL_BOOT, true);
        context.startActivity(intent);
    }

    /**
     * Receiver context
     *
     * @param context receiver context
     * @return boolean
     */
    private static boolean shouldStartupOnBoot(Context context) {
        SettingsManager settingsManager = new SettingsManager(context);
        ConnectionStartupPref pref = settingsManager.getConnectionStartupPref();
        return (pref.getConnectionStartUpPref() != ConnectionStartupPref.DO_NOT_AUTOMATICALLY_CONNECT);
    }

}
