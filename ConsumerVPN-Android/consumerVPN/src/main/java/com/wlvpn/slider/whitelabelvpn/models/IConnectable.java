package com.wlvpn.slider.whitelabelvpn.models;

import android.os.Parcelable;

import java.io.Serializable;

interface IConnectable extends Parcelable, Serializable {

    String getCountryCode();

    String getCountry();

    String getCity();

    String getHostname();

    String getIpAddress();

}
