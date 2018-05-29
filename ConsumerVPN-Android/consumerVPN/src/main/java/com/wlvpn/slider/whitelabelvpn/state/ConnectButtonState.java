package com.wlvpn.slider.whitelabelvpn.state;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("SameParameterValue")
public class ConnectButtonState {

    public static final int CONNECT_ENABLED = 1;
    public static final int CONNECT_DISABLED = 2;
    public static final int CONNECTED = 3;
    public static final int DISCONNECTING = 4;

    @ConnectState
    private int connectState = CONNECT_ENABLED;

    public ConnectButtonState(@ConnectState int connectState) {
        this.connectState = connectState;
    }

    @ConnectState
    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(@ConnectState int connectState) {
        this.connectState = connectState;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECT_ENABLED,
            CONNECT_DISABLED,
            CONNECTED,
            DISCONNECTING})
    public @interface ConnectState {
    }

}
