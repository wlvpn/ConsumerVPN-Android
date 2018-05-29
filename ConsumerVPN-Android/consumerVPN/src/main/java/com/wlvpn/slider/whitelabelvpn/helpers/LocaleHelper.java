package com.wlvpn.slider.whitelabelvpn.helpers;

import java.util.Locale;


public class LocaleHelper {

    /**
     * Gets the display country string by the country code
     *
     * @param countryCode 2 letter representation of country
     * @return String full country name
     */
    public static String getCountryByCode(String countryCode) {
        Locale locale = new Locale("", countryCode);
        return locale.getDisplayCountry(Locale.US);
    }
}
