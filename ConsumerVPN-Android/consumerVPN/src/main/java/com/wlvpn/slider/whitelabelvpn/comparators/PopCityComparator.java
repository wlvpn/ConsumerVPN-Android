package com.wlvpn.slider.whitelabelvpn.comparators;

import com.gentlebreeze.vpn.sdk.model.VpnPop;

import java.util.Comparator;


public class PopCityComparator implements Comparator<VpnPop> {

    @Override
    public int compare(VpnPop left, VpnPop right) {
        return left.getCity().compareTo(right.getCity());
    }
}
