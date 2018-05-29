package com.wlvpn.slider.whitelabelvpn.comparators;

import com.wlvpn.slider.whitelabelvpn.models.Country;

import java.util.Comparator;


public class CountryComparator implements Comparator<Country> {

    @Override
    public int compare(Country left, Country right) {
        return left.getCountryCode().compareTo(right.getCountryCode());
    }
}
