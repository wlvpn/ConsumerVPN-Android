package com.wlvpn.slider.whitelabelvpn.settings;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ConnectionStartupPref {

    public static final int DO_NOT_AUTOMATICALLY_CONNECT = 0;
    public static final int CONNECT_TO_LAST_CONNECTED = 1;
    public static final int CONNECT_TO_FASTEST = 2;
    public static final int CONNECT_TO_FASTEST_IN_COUNTRY = 3;

    @ConnectionStartup
    private int connectionStartUpPref;

    public ConnectionStartupPref(@ConnectionStartup int connectionStartUpPref) {
        this.connectionStartUpPref = connectionStartUpPref;
    }

    public int getConnectionStartUpPref() {
        return connectionStartUpPref;
    }

    public void setConnectionStartUpPref(@ConnectionStartup int connectionStartUpPref) {
        this.connectionStartUpPref = connectionStartUpPref;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DO_NOT_AUTOMATICALLY_CONNECT,
            CONNECT_TO_LAST_CONNECTED,
            CONNECT_TO_FASTEST,
            CONNECT_TO_FASTEST_IN_COUNTRY})
    public @interface ConnectionStartup {
    }


}
