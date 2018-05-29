package com.wlvpn.slider.whitelabelvpn.settings;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ProtocolPref {

    public static final int TCP = 0;
    public static final int UDP = 1;

    @Protocol
    private int protocolPref;

    public ProtocolPref(@Protocol int protocolPref) {
        this.protocolPref = protocolPref;
    }

    public int getProtocolPref() {
        return protocolPref;
    }

    public String getValue() {
        switch (protocolPref) {
            case UDP:
                return "udp";
            case TCP:
                return "tcp";
            default:
                return "udp";
        }
    }

    public void setProtocolPref(@Protocol int protocolPref) {
        this.protocolPref = protocolPref;
    }

    @IntDef({UDP, TCP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Protocol {
    }

}
