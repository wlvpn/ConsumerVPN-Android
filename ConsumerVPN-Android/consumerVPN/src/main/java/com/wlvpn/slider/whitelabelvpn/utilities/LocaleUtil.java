package com.wlvpn.slider.whitelabelvpn.utilities;

import java.util.Locale;

public final class LocaleUtil {

    private LocaleUtil() {
    }

    public static String getCountryDisplay(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry(Locale.US);
    }

    public static String getDefaultCountryCode() {
        return Locale.US.getCountry();
    }

    public static String getDefaultCountryDisplay() {
        return Locale.US.getDisplayCountry();
    }

}
