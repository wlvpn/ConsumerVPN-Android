package com.wlvpn.slider.whitelabelvpn.comparators;

import com.gentlebreeze.vpn.sdk.model.VpnPop;

import java.util.Comparator;

public class PopCountryComparator implements Comparator<VpnPop> {

    @Override
    public int compare(VpnPop left, VpnPop right) {
        int country;
        country = left.getCountry().compareTo(right.getCountry());
        if (country == 0) {
            country = left.getCity().compareToIgnoreCase(right.getCity());
        }
        return country;
    }
}
