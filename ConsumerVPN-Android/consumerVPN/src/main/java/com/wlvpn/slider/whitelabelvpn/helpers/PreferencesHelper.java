package com.wlvpn.slider.whitelabelvpn.helpers;

import android.net.Uri;

import com.wlvpn.slider.whitelabelvpn.BuildConfig;

@SuppressWarnings("SameReturnValue")
public class PreferencesHelper {

    private static PreferencesHelper preferencesHelper;

    public static PreferencesHelper getInstance() {
        if (preferencesHelper == null) {
            preferencesHelper = new PreferencesHelper();
        }
        return preferencesHelper;
    }

    public String getForgotPasswordUrl() {
        return BuildConfig.FORGOT_PASS;
    }

    public String getSignUpUrl() {
        return BuildConfig.SIGN_UP;
    }

    public static Uri getTunKoMarketURL() {
        return Uri.parse("market://details?id=com.aed.tun.installer");
    }

    public static Uri getTunKoMarketWebsiteUrl() {
        return Uri.parse("https://play.google.com/store/apps/details?id=com.aed.tun.installer");
    }

    public static int getServerListCacheTimeout() {
        return 5 * 60;
    }

}