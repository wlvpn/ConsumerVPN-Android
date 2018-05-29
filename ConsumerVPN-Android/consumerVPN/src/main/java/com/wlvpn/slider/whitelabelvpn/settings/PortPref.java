package com.wlvpn.slider.whitelabelvpn.settings;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PortPref {

    public static final int PORT_443 = 0;
    public static final int PORT_1194 = 1;

    @Port
    private int portPref;

    public PortPref(@Port int portPref) {
        this.portPref = portPref;
    }

    public int getPortPref() {
        return portPref;
    }

    public int getValue() {
        switch (portPref) {
            case PORT_443:
                return 443;
            case PORT_1194:
                return 1194;
            default:
                return 443;
        }
    }

    public void setPortPref(@Port int portPref) {
        this.portPref = portPref;
    }

    @IntDef({PORT_443, PORT_1194})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Port {
    }
}
